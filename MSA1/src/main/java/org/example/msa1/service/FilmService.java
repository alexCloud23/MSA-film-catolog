package org.example.msa1.service;

import org.example.msa1.dto.FilmCreateDTO;
import org.example.msa1.dto.FilmDTO;
import org.example.msa1.dto.FilmUpdateDTO;
import org.example.msa1.exception.ResourceNotFoundException;
import org.example.msa1.mapper.FilmMapper;
import org.example.msa1.model.Film;
import org.example.msa1.repository.FilmRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FilmService {

    private final FilmRepository filmRepository;
    private final FilmMapper filmMapper;

    public FilmService(FilmRepository filmRepository, FilmMapper filmMapper) {
        this.filmRepository = filmRepository;
        this.filmMapper = filmMapper;
    }

    public List<FilmDTO> getAllFilms() {
        return filmRepository.findAll().stream()
                .map(filmMapper::toDto)
                .toList();
    }

    public FilmDTO getFilmById(Long id) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Film not found: " + id));
        return filmMapper.toDto(film);
    }

    public FilmDTO getFilmBySlug(String slug) {
        Film film = filmRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Film not found: " + slug));
        return filmMapper.toDto(film);
    }

    public FilmDTO createFilm(FilmCreateDTO dto) {
        Film film = filmMapper.toEntity(dto);
        Film savedFilm = filmRepository.save(film);
        return filmMapper.toDto(savedFilm);
    }

    public FilmDTO updateFilm(Long id, FilmUpdateDTO dto) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Film not found: " + id));
        filmMapper.updateEntityFromDto(dto, film);
        Film updatedFilm = filmRepository.saveAndFlush(film);
        return filmMapper.toDto(updatedFilm);
    }

    public void deleteFilm(Long id) {
        if (!filmRepository.existsById(id)) {
            throw new ResourceNotFoundException("Film not found: " + id);
        }
        filmRepository.deleteById(id);
    }
}