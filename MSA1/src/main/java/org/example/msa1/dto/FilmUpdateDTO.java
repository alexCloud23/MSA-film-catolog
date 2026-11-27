package org.example.msa1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FilmUpdateDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String slug;

    private String description;

    private String genre;

    @Min(1800)
    @Max(2100)
    private Integer releaseYear;

    @Min(1)
    private Integer durationMinutes;
}