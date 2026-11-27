package org.example.msa1.service;

import org.example.msa1.dto.FilmCreateDTO;
import org.example.msa1.dto.FilmDTO;
import org.example.msa1.dto.FilmUpdateDTO;
import org.example.msa1.exception.ResourceNotFoundException;
import org.example.msa1.mapper.FilmMapper;
import org.example.msa1.model.Film;
import org.example.msa1.repository.FilmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private FilmMapper filmMapper;

    @InjectMocks
    private FilmService filmService;

    private Film film;
    private FilmDTO filmDTO;

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setId(1L);
        film.setTitle("Интерстеллар");
        film.setSlug("interstellar");
        film.setDescription("Фантастический фильм");
        film.setGenre("Sci-Fi");
        film.setReleaseYear(2014);
        film.setDurationMinutes(169);
        film.setRating(8.7);
        film.setCreatedAt(LocalDate.now());
        film.setUpdatedAt(LocalDate.now());

        filmDTO = new FilmDTO();
        filmDTO.setId(1L);
        filmDTO.setTitle("Интерстеллар");
        filmDTO.setSlug("interstellar");
        filmDTO.setDescription("Фантастический фильм");
        filmDTO.setGenre("Sci-Fi");
        filmDTO.setReleaseYear(2014);
        filmDTO.setDurationMinutes(169);
        filmDTO.setRating(8.7);
        filmDTO.setCreatedAt(LocalDate.now());
    }

    @Test
    void getAllFilms_shouldReturnAllFilms_whenRepositoryHasFilms() {
        when(filmRepository.findAll()).thenReturn(List.of(film));
        when(filmMapper.toDto(film)).thenReturn(filmDTO);

        List<FilmDTO> result = filmService.getAllFilms();

        assertEquals(1, result.size());
        assertEquals(filmDTO, result.get(0));
        verify(filmRepository, times(1)).findAll();
        verify(filmMapper, times(1)).toDto(film);
    }

    @Test
    void getFilmById_shouldReturnFilm_whenFilmExists() {
        when(filmRepository.findById(1L)).thenReturn(Optional.of(film));
        when(filmMapper.toDto(film)).thenReturn(filmDTO);

        FilmDTO result = filmService.getFilmById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Интерстеллар", result.getTitle());
        verify(filmRepository, times(1)).findById(1L);
        verify(filmMapper, times(1)).toDto(film);
    }

    @Test
    void getFilmById_shouldThrowException_whenFilmDoesNotExist() {
        when(filmRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> filmService.getFilmById(999L));

        assertTrue(exception.getMessage().contains("Film not found: 999"));
        verify(filmRepository, times(1)).findById(999L);
        verify(filmMapper, never()).toDto(any());
    }

    @Test
    void createFilm_shouldCreateFilm_whenValidData() {
        FilmCreateDTO createDTO = new FilmCreateDTO();
        createDTO.setTitle("Интерстеллар");
        createDTO.setSlug("interstellar");
        createDTO.setDescription("Фантастический фильм");
        createDTO.setGenre("Sci-Fi");
        createDTO.setReleaseYear(2014);
        createDTO.setDurationMinutes(169);

        when(filmMapper.toEntity(createDTO)).thenReturn(film);
        when(filmRepository.save(film)).thenReturn(film);
        when(filmMapper.toDto(film)).thenReturn(filmDTO);

        FilmDTO result = filmService.createFilm(createDTO);

        assertNotNull(result);
        assertEquals("Интерстеллар", result.getTitle());
        verify(filmMapper, times(1)).toEntity(createDTO);
        verify(filmRepository, times(1)).save(film);
        verify(filmMapper, times(1)).toDto(film);
    }

    @Test
    void updateFilm_shouldUpdateFilm_whenFilmExists() {
        FilmUpdateDTO updateDTO = new FilmUpdateDTO();
        updateDTO.setTitle("Интерстеллар 2");
        updateDTO.setSlug("interstellar-2");
        updateDTO.setDescription("Обновлённое описание");
        updateDTO.setGenre("Sci-Fi");
        updateDTO.setReleaseYear(2015);
        updateDTO.setDurationMinutes(180);

        when(filmRepository.findById(1L)).thenReturn(Optional.of(film));
        when(filmRepository.saveAndFlush(film)).thenReturn(film);

        FilmDTO updatedFilmDTO = new FilmDTO();
        updatedFilmDTO.setId(1L);
        updatedFilmDTO.setTitle("Интерстеллар 2");
        updatedFilmDTO.setSlug("interstellar-2");
        updatedFilmDTO.setDescription("Обновлённое описание");
        updatedFilmDTO.setGenre("Sci-Fi");
        updatedFilmDTO.setReleaseYear(2015);
        updatedFilmDTO.setDurationMinutes(180);
        updatedFilmDTO.setRating(8.7);
        updatedFilmDTO.setCreatedAt(LocalDate.now());

        when(filmMapper.toDto(film)).thenReturn(updatedFilmDTO);

        FilmDTO result = filmService.updateFilm(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Интерстеллар 2", result.getTitle());
        verify(filmRepository, times(1)).findById(1L);
        verify(filmRepository, times(1)).saveAndFlush(film);
        verify(filmMapper, times(1)).updateEntityFromDto(any(FilmUpdateDTO.class), any(Film.class));
    }

    @Test
    void updateFilm_shouldThrowException_whenFilmDoesNotExist() {
        FilmUpdateDTO updateDTO = new FilmUpdateDTO();
        updateDTO.setTitle("Новый заголовок");
        updateDTO.setSlug("new-slug");

        when(filmRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> filmService.updateFilm(999L, updateDTO));

        assertTrue(exception.getMessage().contains("Film not found: 999"));
        verify(filmRepository, times(1)).findById(999L);
        verify(filmRepository, never()).save(any());
    }

    @Test
    void deleteFilm_shouldDeleteFilm_whenFilmExists() {
        when(filmRepository.existsById(1L)).thenReturn(true);
        doNothing().when(filmRepository).deleteById(1L);

        filmService.deleteFilm(1L);

        verify(filmRepository, times(1)).existsById(1L);
        verify(filmRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteFilm_shouldThrowException_whenFilmDoesNotExist() {
        when(filmRepository.existsById(999L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> filmService.deleteFilm(999L));

        assertTrue(exception.getMessage().contains("Film not found: 999"));
        verify(filmRepository, times(1)).existsById(999L);
        verify(filmRepository, never()).deleteById(anyLong());
    }

    @Test
    void getFilmBySlug_shouldReturnFilm_whenSlugExists() {
        when(filmRepository.findBySlug("interstellar")).thenReturn(Optional.of(film));
        when(filmMapper.toDto(film)).thenReturn(filmDTO);

        FilmDTO result = filmService.getFilmBySlug("interstellar");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Интерстеллар", result.getTitle());
        verify(filmRepository, times(1)).findBySlug("interstellar");
        verify(filmMapper, times(1)).toDto(film);
    }

    @Test
    void getFilmBySlug_shouldThrowException_whenSlugDoesNotExist() {
        when(filmRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> filmService.getFilmBySlug("nonexistent"));

        assertTrue(exception.getMessage().contains("Film not found: nonexistent"));
        verify(filmRepository, times(1)).findBySlug("nonexistent");
    }
}
