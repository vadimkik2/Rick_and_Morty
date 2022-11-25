package mate.academy.springboot.rickandmortyapp.service;

import java.util.List;
import mate.academy.springboot.rickandmortyapp.model.MovieCharacter;

public interface MovieCharacterService {
    void syncExternalCharacters();

    MovieCharacter getRandomCharacter();

    List<MovieCharacter> findAlByNameContains(String namePart);
}
