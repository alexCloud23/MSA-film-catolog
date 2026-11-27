package org.example.catalogclient;

import feign.Capability;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "films", configuration = FilmClient.Configuration.class)
public interface FilmClient {
    @GetMapping("/api/films")
    List<Film> getFilms();

    @GetMapping("/api/films/{id}")
    Film getFilm(@PathVariable("id") Long id);

    class Configuration {
        @Bean
        Capability filmClientLogging() {
            return new FilmClientLogging();
        }
    }
}
