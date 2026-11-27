package org.example.msa1.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static com.jayway.jsonpath.JsonPath.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class FilmCrudIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private String uniqueSlug() {
        return "film-" + UUID.randomUUID();
    }

    private String filmJson(String slug, String title) {
        return """
                {
                  "title": "%s",
                  "slug": "%s",
                  "description": "Эпическое приключение по мотивам поэмы Гомера",
                  "genre": "Adventure",
                  "releaseYear": 2026,
                  "durationMinutes": 148
                }
                """.formatted(title, slug);
    }

    @Test
    void fullCrudCycle_shouldPass() throws Exception {
        String slug = uniqueSlug();

        MvcResult createdResult = mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson(slug, "Одиссея 2026")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Одиссея 2026"))
                .andExpect(jsonPath("$.slug").value(slug))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty())
                .andReturn();

        String createdBody = createdResult.getResponse().getContentAsString();
        long filmId = ((Number) parse(createdBody).read("$.id")).longValue();

        mockMvc.perform(get("/api/films/" + filmId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(filmId))
                .andExpect(jsonPath("$.slug").value(slug));

        mockMvc.perform(put("/api/films/" + filmId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson(slug, "Одиссея 2026 (обновлено)")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(filmId))
                .andExpect(jsonPath("$.title").value("Одиссея 2026 (обновлено)"))
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());

        mockMvc.perform(get("/api/films/slug/" + slug))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(filmId))
                .andExpect(jsonPath("$.slug").value(slug));

        mockMvc.perform(delete("/api/films/" + filmId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/films/" + filmId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Film not found: " + filmId));
    }

    @Test
    void getAllFilms_shouldReturnCreatedFilm() throws Exception {
        String slug = uniqueSlug();

        mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson(slug, "Всё везде и сразу")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.slug == '" + slug + "')]").isNotEmpty());
    }

    @Test
    void getUnknownSlug_shouldReturn404WithErrorBody() throws Exception {
        mockMvc.perform(get("/api/films/slug/no-such-slug"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Film not found: no-such-slug"))
                .andExpect(jsonPath("$.details").doesNotExist());
    }

    @Test
    void createInvalidFilm_shouldReturn400WithDetails() throws Exception {
        String invalidJson = """
                {"title": "", "slug": "", "releaseYear": 1799, "durationMinutes": 0}
                """;

        MvcResult result = mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details.length()").value(4))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThat(body).contains("title").contains("slug").contains("releaseYear").contains("durationMinutes");
    }
}
