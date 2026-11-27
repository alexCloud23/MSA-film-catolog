package org.example.msa1.mapper;

import org.example.msa1.dto.FilmCreateDTO;
import org.example.msa1.dto.FilmDTO;
import org.example.msa1.dto.FilmUpdateDTO;
import org.example.msa1.model.Film;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmMapperTest {

    private FilmMapper filmMapper;
    private Film film;

    @BeforeEach
    void setUp() {
        filmMapper = new FilmMapperImpl();

        film = new Film();
        film.setId(1L);
        film.setSlug("interstellar");
        film.setTitle("Интерстеллар");
        film.setDescription("Фантастический фильм");
        film.setGenre("Sci-Fi");
        film.setReleaseYear(2014);
        film.setDurationMinutes(169);
        film.setRating(8.7);
        film.setCreatedAt(LocalDate.of(2014, 11, 7));
        film.setUpdatedAt(LocalDate.of(2014, 11, 8));
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        assertNull(filmMapper.toEntity(null));
    }

    @Test
    void toEntity_shouldMapAllFields() {
        FilmCreateDTO dto = new FilmCreateDTO();
        dto.setSlug("interstellar");
        dto.setTitle("Интерстеллар");
        dto.setDescription("Фантастический фильм");
        dto.setGenre("Sci-Fi");
        dto.setReleaseYear(2014);
        dto.setDurationMinutes(169);

        Film result = filmMapper.toEntity(dto);

        assertNotNull(result);
        assertEquals(dto.getSlug(), result.getSlug());
        assertEquals(dto.getTitle(), result.getTitle());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getGenre(), result.getGenre());
        assertEquals(dto.getReleaseYear(), result.getReleaseYear());
        assertEquals(dto.getDurationMinutes(), result.getDurationMinutes());
    }

    @Test
    void toDto_shouldReturnNull_whenEntityIsNull() {
        assertNull(filmMapper.toDto(null));
    }

    @Test
    void toDto_shouldMapAllFields() {
        FilmDTO result = filmMapper.toDto(film);

        assertNotNull(result);
        assertEquals(film.getId(), result.getId());
        assertEquals(film.getTitle(), result.getTitle());
        assertEquals(film.getSlug(), result.getSlug());
        assertEquals(film.getDescription(), result.getDescription());
        assertEquals(film.getGenre(), result.getGenre());
        assertEquals(film.getReleaseYear(), result.getReleaseYear());
        assertEquals(film.getDurationMinutes(), result.getDurationMinutes());
        assertEquals(film.getRating(), result.getRating());
        assertEquals(film.getCreatedAt(), result.getCreatedAt());
    }

    @Test
    void updateEntityFromDto_shouldDoNothing_whenDtoIsNull() {
        filmMapper.updateEntityFromDto(null, film);

        assertEquals("Интерстеллар", film.getTitle());
        assertEquals("interstellar", film.getSlug());
    }

    @Test
    void updateEntityFromDto_shouldUpdateAllNonNullFields() {
        FilmUpdateDTO dto = new FilmUpdateDTO();
        dto.setTitle("Интерстеллар 2");
        dto.setSlug("interstellar-2");
        dto.setDescription("Новое описание");
        dto.setGenre("Drama");
        dto.setReleaseYear(2015);
        dto.setDurationMinutes(180);

        filmMapper.updateEntityFromDto(dto, film);

        assertEquals("Интерстеллар 2", film.getTitle());
        assertEquals("interstellar-2", film.getSlug());
        assertEquals("Новое описание", film.getDescription());
        assertEquals("Drama", film.getGenre());
        assertEquals(2015, film.getReleaseYear());
        assertEquals(180, film.getDurationMinutes());
    }

    @Test
    void updateEntityFromDto_shouldIgnoreAllNullFields() {
        FilmUpdateDTO dto = new FilmUpdateDTO();
        dto.setTitle(null);
        dto.setSlug(null);
        dto.setDescription(null);
        dto.setGenre(null);
        dto.setReleaseYear(null);
        dto.setDurationMinutes(null);

        filmMapper.updateEntityFromDto(dto, film);

        assertEquals("Интерстеллар", film.getTitle());
        assertEquals("interstellar", film.getSlug());
        assertEquals("Фантастический фильм", film.getDescription());
        assertEquals("Sci-Fi", film.getGenre());
        assertEquals(2014, film.getReleaseYear());
        assertEquals(169, film.getDurationMinutes());
    }
}
