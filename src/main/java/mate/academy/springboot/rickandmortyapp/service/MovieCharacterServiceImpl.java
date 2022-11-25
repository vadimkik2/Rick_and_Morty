package mate.academy.springboot.rickandmortyapp.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import mate.academy.springboot.rickandmortyapp.dto.external.ApiCharacterDto;
import mate.academy.springboot.rickandmortyapp.dto.external.ApiResponseDto;
import mate.academy.springboot.rickandmortyapp.dto.mapper.MovieCharacterMapper;
import mate.academy.springboot.rickandmortyapp.model.MovieCharacter;
import mate.academy.springboot.rickandmortyapp.repository.MovieCharacterRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class MovieCharacterServiceImpl implements MovieCharacterService {
    private final HttpClient httpClient;
    private final MovieCharacterRepository movieCharacterRepository;
    private final MovieCharacterMapper mapper;

    public MovieCharacterServiceImpl(HttpClient httpClient,
                                     MovieCharacterRepository movieCharacterRepository,
                                     MovieCharacterMapper mapper) {
        this.httpClient = httpClient;
        this.movieCharacterRepository = movieCharacterRepository;
        this.mapper = mapper;
    }

    @PostConstruct
    @Scheduled(cron = "0 0 15 * * ?")
    @Override
    public void syncExternalCharacters() {
        log.info("syncExternalCharacters method was invoke at: " + LocalDateTime.now());
        ApiResponseDto apiResponseDto = httpClient.get("https://rickandmortyapi.com/api/character",
                ApiResponseDto.class);

        saveDtosToDb(apiResponseDto);

        while (apiResponseDto.getInfo().getNext() != null) {
            apiResponseDto = httpClient.get(apiResponseDto.getInfo().getNext(),
                    ApiResponseDto.class);
            saveDtosToDb(apiResponseDto);
        }
    }

    @Override
    public MovieCharacter getRandomCharacter() {
        long count = movieCharacterRepository.count();
        long randomId = (long) (Math.random() * count);
        return movieCharacterRepository.getById(randomId);
    }

    @Override
    public List<MovieCharacter> findAlByNameContains(String namePart) {
        return movieCharacterRepository.findAlByNameContains(namePart);
    }

    public List<MovieCharacter> saveDtosToDb(ApiResponseDto responseDto) {
        Map<Long, ApiCharacterDto> externalDtos = Arrays.stream(responseDto.getResults())
                .collect(Collectors.toMap(ApiCharacterDto::getId, Function.identity()));

        Set<Long> externalId = externalDtos.keySet();

        List<MovieCharacter> existingCharacters = movieCharacterRepository
                .findAllByExternalIdIn(externalId);

        Map<Long, MovieCharacter> existingCharactersWithId = existingCharacters.stream()
                .collect(Collectors.toMap(MovieCharacter::getExternalId, Function.identity()));

        Set<Long> existingIds = existingCharactersWithId.keySet();

        externalId.removeAll(existingIds);

        List<MovieCharacter> charactersToSave = externalId.stream()
                .map(i -> mapper.parseMovieCharacterResponseDto(externalDtos.get(i)))
                .collect(Collectors.toList());
        return movieCharacterRepository.saveAll(charactersToSave);
    }
}
