package mate.academy.springboot.rickandmortyapp.repository;

import java.util.List;
import java.util.Set;
import mate.academy.springboot.rickandmortyapp.model.MovieCharacter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieCharacterRepository extends JpaRepository<MovieCharacter,Long> {
    List<MovieCharacter> findAllByExternalIdIn(Set<Long> externalId);

    List<MovieCharacter> findAllByNameContains(String namePart);
}
