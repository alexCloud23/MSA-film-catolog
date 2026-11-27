package org.example.msa1.dto;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FilmDTO {

    private Long id;
    private String title;
    private String slug;
    private String description;
    private String genre;
    private Integer releaseYear;
    private Integer durationMinutes;
    private Double rating;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}