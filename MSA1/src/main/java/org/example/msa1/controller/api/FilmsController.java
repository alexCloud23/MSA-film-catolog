package org.example.msa1.controller.api;

import org.example.msa1.dto.FilmCreateDTO;
import org.example.msa1.dto.FilmDTO;
import org.example.msa1.dto.FilmUpdateDTO;
import org.example.msa1.service.FilmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/films")
public class FilmsController {

    private final FilmService filmService;

    public FilmsController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public ResponseEntity<List<FilmDTO>> getAllFilms() {
        List<FilmDTO> films = filmService.getAllFilms();
        return ResponseEntity.ok(films);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmDTO> getFilmById(@PathVariable Long id) {
        FilmDTO film = filmService.getFilmById(id);
        return ResponseEntity.ok(film);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<FilmDTO> getFilmBySlug(@PathVariable String slug) {
        FilmDTO film = filmService.getFilmBySlug(slug);
        return ResponseEntity.ok(film);
    }

    @PostMapping
    public ResponseEntity<FilmDTO> createFilm(@Valid @RequestBody FilmCreateDTO dto) {
        FilmDTO createdFilm = filmService.createFilm(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FilmDTO> updateFilm(@PathVariable Long id, @Valid @RequestBody FilmUpdateDTO dto) {
        FilmDTO updatedFilm = filmService.updateFilm(id, dto);
        return ResponseEntity.ok(updatedFilm);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilm(@PathVariable Long id) {
        filmService.deleteFilm(id);
        return ResponseEntity.noContent().build();
    }
}