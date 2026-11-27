package org.example.msa1.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.msa1.dto.FilmCreateDTO;
import org.example.msa1.dto.ReviewCreateDTO;
import org.example.msa1.dto.ReviewUpdateDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DtoValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDownValidator() {
        validatorFactory.close();
    }

    private FilmCreateDTO validFilmCreateDTO() {
        FilmCreateDTO dto = new FilmCreateDTO();
        dto.setTitle("Интерстеллар");
        dto.setSlug("interstellar");
        dto.setDescription("Фантастический фильм");
        dto.setGenre("Sci-Fi");
        dto.setReleaseYear(2014);
        dto.setDurationMinutes(169);
        return dto;
    }

    private ReviewCreateDTO validReviewCreateDTO() {
        ReviewCreateDTO dto = new ReviewCreateDTO();
        dto.setAuthorId(1L);
        dto.setText("Отличный фильм!");
        dto.setRating(5);
        return dto;
    }

    private ReviewUpdateDTO validReviewUpdateDTO() {
        ReviewUpdateDTO dto = new ReviewUpdateDTO();
        dto.setText("Отличный фильм!");
        dto.setRating(5);
        return dto;
    }

    @Test
    void filmCreateDTO_shouldBeValid_whenAllFieldsCorrect() {
        assertTrue(validator.validate(validFilmCreateDTO()).isEmpty());
    }

    @Test
    void filmCreateDTO_shouldRejectBlankTitleAndSlug() {
        FilmCreateDTO dto = validFilmCreateDTO();
        dto.setTitle("   ");
        dto.setSlug("");

        Set<ConstraintViolation<FilmCreateDTO>> violations = validator.validate(dto);

        assertEquals(2, violations.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {1799, 2101})
    void filmCreateDTO_shouldRejectReleaseYearOutsideBounds(int releaseYear) {
        FilmCreateDTO dto = validFilmCreateDTO();
        dto.setReleaseYear(releaseYear);

        assertFalse(validator.validate(dto).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -100})
    void filmCreateDTO_shouldRejectDurationBelowMinimum(int durationMinutes) {
        FilmCreateDTO dto = validFilmCreateDTO();
        dto.setDurationMinutes(durationMinutes);

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void reviewCreateDTO_shouldRejectNullAuthorId() {
        ReviewCreateDTO dto = validReviewCreateDTO();
        dto.setAuthorId(null);

        Set<ConstraintViolation<ReviewCreateDTO>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 6})
    void reviewCreateDTO_shouldRejectRatingOutsideBounds(int rating) {
        ReviewCreateDTO dto = validReviewCreateDTO();
        dto.setRating(rating);

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void reviewUpdateDTO_shouldRejectBlankText() {
        ReviewUpdateDTO dto = validReviewUpdateDTO();
        dto.setText("");

        Set<ConstraintViolation<ReviewUpdateDTO>> violations = validator.validate(dto);

        assertEquals(1, violations.size());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 6})
    void reviewUpdateDTO_shouldRejectRatingOutsideBounds(int rating) {
        ReviewUpdateDTO dto = validReviewUpdateDTO();
        dto.setRating(rating);

        assertFalse(validator.validate(dto).isEmpty());
    }
}
