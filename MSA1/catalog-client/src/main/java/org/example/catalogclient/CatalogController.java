package org.example.catalogclient;

import feign.FeignException;
import feign.RetryableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/catalog/films")
class CatalogController {
    private final FilmClient client;

    CatalogController(FilmClient client) {
        this.client = client;
    }

    @GetMapping
    List<Film> getFilms() {
        return client.getFilms();
    }

    @GetMapping("/{id}")
    Film getFilm(@PathVariable Long id) {
        return client.getFilm(id);
    }

    @ExceptionHandler(FeignException.class)
    ProblemDetail handleCatalogFailure(FeignException exception) {
        if (exception.status() == 404) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Film not found");
        }
        if (exception instanceof RetryableException || exception.status() == 503) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, "Film catalog is unavailable");
        }
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, "Film catalog returned an unexpected response");
    }
}
