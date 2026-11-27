package org.example.msa1.integration;

import org.example.msa1.model.Film;
import org.example.msa1.model.User;
import org.example.msa1.repository.FilmRepository;
import org.example.msa1.repository.UserRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class ReviewCrudIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilmRepository filmRepository;

    private MockMvc mockMvc;

    private User author;
    private Film film;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        author = new User();
        author.setName("Алексей Лысенко");
        author.setEmail("author-" + UUID.randomUUID() + "@example.com");
        author = userRepository.save(author);

        film = new Film();
        film.setTitle("Отель Гранд Будапешт");
        film.setSlug("film-" + UUID.randomUUID());
        film.setReleaseYear(2014);
        film.setDurationMinutes(99);
        film = filmRepository.save(film);
    }

    private String reviewCreateJson() {
        return """
                {
                  "authorId": %d,
                  "text": "Отличная атмосферная комедия",
                  "rating": 5
                }
                """.formatted(author.getId());
    }

    private String reviewUpdateJson() {
        return """
                {
                  "text": "После второго просмотра понравилось ещё больше",
                  "rating": 4
                }
                """;
    }

    @Test
    void fullReviewCrudCycle_shouldPass() throws Exception {
        MvcResult createdResult = mockMvc.perform(post("/api/reviews/film/" + film.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewCreateJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.filmId").value(film.getId()))
                .andExpect(jsonPath("$.authorId").value(author.getId()))
                .andExpect(jsonPath("$.text").value("Отличная атмосферная комедия"))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andReturn();

        String createdBody = createdResult.getResponse().getContentAsString();
        long reviewId = ((Number) parse(createdBody).read("$.id")).longValue();

        mockMvc.perform(get("/api/reviews/film/" + film.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(reviewId))
                .andExpect(jsonPath("$[0].text").value("Отличная атмосферная комедия"));

        mockMvc.perform(put("/api/reviews/" + reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewUpdateJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewId))
                .andExpect(jsonPath("$.text").value("После второго просмотра понравилось ещё больше"))
                .andExpect(jsonPath("$.rating").value(4));

        mockMvc.perform(delete("/api/reviews/" + reviewId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/reviews/" + reviewId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Review not found: " + reviewId));
    }

    @Test
    void getAllReviews_shouldReturnArray() throws Exception {
        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void createReviewForUnknownFilm_shouldReturn404WithErrorBody() throws Exception {
        mockMvc.perform(post("/api/reviews/film/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewCreateJson()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Film not found: 999999"));
    }

    @Test
    void createReviewWithUnknownAuthor_shouldReturn404WithErrorBody() throws Exception {
        String json = """
                {
                  "authorId": 999999,
                  "text": "Отзыв несуществующего автора",
                  "rating": 5
                }
                """;

        mockMvc.perform(post("/api/reviews/film/" + film.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("User not found: 999999"));
    }

    @Test
    void getReviewsForUnknownFilm_shouldReturn404WithErrorBody() throws Exception {
        mockMvc.perform(get("/api/reviews/film/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Film not found: 999999"));
    }
}
