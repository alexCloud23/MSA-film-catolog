package org.example.msa1.mapper;

import org.example.msa1.dto.ReviewCreateDTO;
import org.example.msa1.dto.ReviewDTO;
import org.example.msa1.dto.ReviewUpdateDTO;
import org.example.msa1.model.Film;
import org.example.msa1.model.Review;
import org.example.msa1.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReviewMapperTest {

    private ReviewMapper reviewMapper;
    private Review review;

    @BeforeEach
    void setUp() {
        reviewMapper = new ReviewMapperImpl();

        Film film = new Film();
        film.setId(1L);
        film.setTitle("Интерстеллар");
        film.setSlug("interstellar");

        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        review = new Review();
        review.setId(1L);
        review.setFilm(film);
        review.setAuthor(user);
        review.setText("Отличный фильм!");
        review.setRating(5);
        review.setCreatedAt(LocalDate.of(2026, 1, 15));
        review.setUpdatedAt(LocalDate.of(2026, 1, 16));
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        assertNull(reviewMapper.toEntity(null));
    }

    @Test
    void toEntity_shouldMapTextAndRating() {
        ReviewCreateDTO dto = new ReviewCreateDTO();
        dto.setAuthorId(1L);
        dto.setText("Отличный фильм!");
        dto.setRating(5);

        Review result = reviewMapper.toEntity(dto);

        assertNotNull(result);
        assertEquals("Отличный фильм!", result.getText());
        assertEquals(5, result.getRating());
        assertNull(result.getFilm());
        assertNull(result.getAuthor());
    }

    @Test
    void toDto_shouldReturnNull_whenEntityIsNull() {
        assertNull(reviewMapper.toDto(null));
    }

    @Test
    void toDto_shouldMapNullIds_whenFilmAndAuthorAreNull() {
        review.setFilm(null);
        review.setAuthor(null);

        ReviewDTO result = reviewMapper.toDto(review);

        assertNotNull(result);
        assertNull(result.getFilmId());
        assertNull(result.getAuthorId());
    }

    @Test
    void toDto_shouldMapAllMappedFields() {
        ReviewDTO result = reviewMapper.toDto(review);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getFilmId());
        assertEquals(1L, result.getAuthorId());
        assertEquals("Отличный фильм!", result.getText());
        assertEquals(5, result.getRating());
        assertEquals(review.getCreatedAt(), result.getCreatedAt());
    }

    @Test
    void updateEntityFromDto_shouldDoNothing_whenDtoIsNull() {
        reviewMapper.updateEntityFromDto(null, review);

        assertEquals("Отличный фильм!", review.getText());
        assertEquals(5, review.getRating());
    }

    @Test
    void updateEntityFromDto_shouldUpdateNonNullFields() {
        ReviewUpdateDTO dto = new ReviewUpdateDTO();
        dto.setText("Обновлённый текст");
        dto.setRating(4);

        reviewMapper.updateEntityFromDto(dto, review);

        assertEquals("Обновлённый текст", review.getText());
        assertEquals(4, review.getRating());
    }

    @Test
    void updateEntityFromDto_shouldIgnoreAllNullFields() {
        ReviewUpdateDTO dto = new ReviewUpdateDTO();
        dto.setText(null);
        dto.setRating(null);

        reviewMapper.updateEntityFromDto(dto, review);

        assertEquals("Отличный фильм!", review.getText());
        assertEquals(5, review.getRating());
    }
}
