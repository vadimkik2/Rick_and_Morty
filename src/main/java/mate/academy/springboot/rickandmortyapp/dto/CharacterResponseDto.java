package mate.academy.springboot.rickandmortyapp.dto;

import lombok.Data;
import mate.academy.springboot.rickandmortyapp.model.Gender;
import mate.academy.springboot.rickandmortyapp.model.Status;

@Data
public class CharacterResponseDto {
    private Long id;
    private Long externalId;
    private String name;
    private Status status;
    private Gender gender;
}
