package org.example.catalogclient;

import java.time.LocalDate;

public record Film(
        Long id,
        String title,
        String slug,
        String description,
        String genre,
        Integer releaseYear,
        Integer durationMinutes,
        Double rating,
        LocalDate createdAt,
        LocalDate updatedAt
) {
}
