package org.example.msa1.dto;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReviewDTO {

    private Long id;
    private Long filmId;
    private Long authorId;
    private String text;
    private Integer rating;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}