package org.example.msa1.controller.api;

import org.example.msa1.dto.FilmCreateDTO;
import org.example.msa1.dto.FilmDTO;
import org.example.msa1.dto.FilmUpdateDTO;
import org.example.msa1.exception.GlobalExceptionHandler;
import org.example.msa1.exception.ResourceNotFoundException;
import org.example.msa1.service.FilmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FilmsControllerTest {

    private static final String VALID_FILM_JSON = """
            {
              "title": "Интерстеллар",
              "slug": "interstellar",
              "description": "Фантастический фильм",
              "genre": "Sci-Fi",
              "releaseYear": 2014,
              "durationMinutes": 169
            }
            """;

    @Mock
    private FilmService filmService;

    @InjectMocks
    private FilmsController filmsController;

    private MockMvc mockMvc;

    private FilmDTO filmDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(filmsController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        filmDTO = new FilmDTO();
        filmDTO.setId(1L);
        filmDTO.setTitle("Интерстеллар");
        filmDTO.setSlug("interstellar");
        filmDTO.setDescription("Фантастический фильм");
        filmDTO.setGenre("Sci-Fi");
        filmDTO.setReleaseYear(2014);
        filmDTO.setDurationMinutes(169);
        filmDTO.setRating(8.7);
        filmDTO.setCreatedAt(LocalDate.of(2014, 11, 7));
    }

    @Test
    void getAllFilms_shouldReturnOkWithFilmsList() throws Exception {
        when(filmService.getAllFilms()).thenReturn(List.of(filmDTO));

        mockMvc.perform(get("/api/films"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Интерстеллар"))
                .andExpect(jsonPath("$[0].slug").value("interstellar"));

        verify(filmService, times(1)).getAllFilms();
    }

    @Test
    void getAllFilms_shouldReturnEmptyList_whenNoFilmsExist() throws Exception {
        when(filmService.getAllFilms()).thenReturn(List.of());

        mockMvc.perform(get("/api/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getFilmById_shouldReturnOk_whenFilmExists() throws Exception {
        when(filmService.getFilmById(1L)).thenReturn(filmDTO);

        mockMvc.perform(get("/api/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Интерстеллар"));

        verify(filmService, times(1)).getFilmById(1L);
    }

    @Test
    void getFilmById_shouldReturnNotFound_whenFilmDoesNotExist() throws Exception {
        when(filmService.getFilmById(999L)).thenThrow(new ResourceNotFoundException("Film not found: 999"));

        mockMvc.perform(get("/api/films/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFilmBySlug_shouldReturnOk_whenFilmExists() throws Exception {
        when(filmService.getFilmBySlug("interstellar")).thenReturn(filmDTO);

        mockMvc.perform(get("/api/films/slug/interstellar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.slug").value("interstellar"));

        verify(filmService, times(1)).getFilmBySlug("interstellar");
    }

    @Test
    void getFilmBySlug_shouldReturnNotFound_whenFilmDoesNotExist() throws Exception {
        when(filmService.getFilmBySlug("nonexistent"))
                .thenThrow(new ResourceNotFoundException("Film not found: nonexistent"));

        mockMvc.perform(get("/api/films/slug/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createFilm_shouldReturnCreated_whenValidData() throws Exception {
        when(filmService.createFilm(any(FilmCreateDTO.class))).thenReturn(filmDTO);

        mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_FILM_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Интерстеллар"));

        verify(filmService, times(1)).createFilm(any(FilmCreateDTO.class));
    }

    @Test
    void createFilm_shouldReturnBadRequest_whenValidationFails() throws Exception {
        String invalidJson = """
                {"title": "", "slug": "", "releaseYear": 1799, "durationMinutes": 0}
                """;

        mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(filmService, never()).createFilm(any());
    }

    @Test
    void updateFilm_shouldReturnOk_whenFilmExists() throws Exception {
        when(filmService.updateFilm(eq(1L), any(FilmUpdateDTO.class))).thenReturn(filmDTO);

        mockMvc.perform(put("/api/films/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_FILM_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Интерстеллар"));

        verify(filmService, times(1)).updateFilm(eq(1L), any(FilmUpdateDTO.class));
    }

    @Test
    void updateFilm_shouldReturnNotFound_whenFilmDoesNotExist() throws Exception {
        when(filmService.updateFilm(eq(999L), any(FilmUpdateDTO.class)))
                .thenThrow(new ResourceNotFoundException("Film not found: 999"));

        mockMvc.perform(put("/api/films/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_FILM_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateFilm_shouldReturnBadRequest_whenServiceThrowsIllegalArgument() throws Exception {
        when(filmService.updateFilm(eq(1L), any(FilmUpdateDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(put("/api/films/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_FILM_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteFilm_shouldReturnNoContent_whenFilmExists() throws Exception {
        mockMvc.perform(delete("/api/films/1"))
                .andExpect(status().isNoContent());

        verify(filmService, times(1)).deleteFilm(1L);
    }

    @Test
    void deleteFilm_shouldReturnNotFound_whenFilmDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Film not found: 999")).when(filmService).deleteFilm(999L);

        mockMvc.perform(delete("/api/films/999"))
                .andExpect(status().isNotFound());
    }
}
