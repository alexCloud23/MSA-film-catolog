package org.example.msa1.controller.api;

import org.example.msa1.dto.ReviewCreateDTO;
import org.example.msa1.dto.ReviewDTO;
import org.example.msa1.dto.ReviewUpdateDTO;
import org.example.msa1.exception.GlobalExceptionHandler;
import org.example.msa1.exception.ResourceNotFoundException;
import org.example.msa1.service.ReviewService;
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
class ReviewsControllerTest {

    private static final String VALID_REVIEW_JSON = """
            {
              "authorId": 1,
              "text": "Отличный фильм!",
              "rating": 5
            }
            """;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewsController reviewsController;

    private MockMvc mockMvc;

    private ReviewDTO reviewDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewsController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        reviewDTO = new ReviewDTO();
        reviewDTO.setId(1L);
        reviewDTO.setFilmId(1L);
        reviewDTO.setAuthorId(1L);
        reviewDTO.setText("Отличный фильм!");
        reviewDTO.setRating(5);
        reviewDTO.setCreatedAt(LocalDate.of(2026, 1, 15));
    }

    @Test
    void getAllReviews_shouldReturnOkWithReviewsList() throws Exception {
        when(reviewService.getAllReviews()).thenReturn(List.of(reviewDTO));

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].text").value("Отличный фильм!"))
                .andExpect(jsonPath("$[0].rating").value(5));

        verify(reviewService, times(1)).getAllReviews();
    }

    @Test
    void getAllReviews_shouldReturnEmptyList_whenNoReviewsExist() throws Exception {
        when(reviewService.getAllReviews()).thenReturn(List.of());

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getReviewById_shouldReturnOk_whenReviewExists() throws Exception {
        when(reviewService.getReviewById(1L)).thenReturn(reviewDTO);

        mockMvc.perform(get("/api/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Отличный фильм!"));

        verify(reviewService, times(1)).getReviewById(1L);
    }

    @Test
    void getReviewById_shouldReturnNotFound_whenReviewDoesNotExist() throws Exception {
        when(reviewService.getReviewById(999L)).thenThrow(new ResourceNotFoundException("Review not found: 999"));

        mockMvc.perform(get("/api/reviews/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getReviewsByFilmId_shouldReturnOk_whenFilmExists() throws Exception {
        when(reviewService.getReviewsByFilmId(1L)).thenReturn(List.of(reviewDTO));

        mockMvc.perform(get("/api/reviews/film/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].filmId").value(1))
                .andExpect(jsonPath("$[0].authorId").value(1));

        verify(reviewService, times(1)).getReviewsByFilmId(1L);
    }

    @Test
    void getReviewsByFilmId_shouldReturnNotFound_whenFilmDoesNotExist() throws Exception {
        when(reviewService.getReviewsByFilmId(999L)).thenThrow(new ResourceNotFoundException("Film not found: 999"));

        mockMvc.perform(get("/api/reviews/film/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createReview_shouldReturnCreated_whenValidData() throws Exception {
        when(reviewService.createReview(eq(1L), any(ReviewCreateDTO.class))).thenReturn(reviewDTO);

        mockMvc.perform(post("/api/reviews/film/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REVIEW_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Отличный фильм!"));

        verify(reviewService, times(1)).createReview(eq(1L), any(ReviewCreateDTO.class));
    }

    @Test
    void createReview_shouldReturnNotFound_whenFilmDoesNotExist() throws Exception {
        when(reviewService.createReview(eq(999L), any(ReviewCreateDTO.class)))
                .thenThrow(new ResourceNotFoundException("Film not found: 999"));

        mockMvc.perform(post("/api/reviews/film/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REVIEW_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createReview_shouldReturnBadRequest_whenValidationFails() throws Exception {
        String invalidJson = """
                {"authorId": null, "text": "", "rating": 6}
                """;

        mockMvc.perform(post("/api/reviews/film/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(reviewService, never()).createReview(any(), any());
    }

    @Test
    void updateReview_shouldReturnOk_whenReviewExists() throws Exception {
        ReviewDTO updatedDTO = new ReviewDTO();
        updatedDTO.setId(1L);
        updatedDTO.setFilmId(1L);
        updatedDTO.setAuthorId(1L);
        updatedDTO.setText("Обновлённый текст");
        updatedDTO.setRating(4);
        updatedDTO.setCreatedAt(LocalDate.of(2026, 1, 15));

        when(reviewService.updateReview(eq(1L), any(ReviewUpdateDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text": "Обновлённый текст", "rating": 4}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Обновлённый текст"))
                .andExpect(jsonPath("$.rating").value(4));

        verify(reviewService, times(1)).updateReview(eq(1L), any(ReviewUpdateDTO.class));
    }

    @Test
    void updateReview_shouldReturnNotFound_whenReviewDoesNotExist() throws Exception {
        when(reviewService.updateReview(eq(999L), any(ReviewUpdateDTO.class)))
                .thenThrow(new ResourceNotFoundException("Review not found: 999"));

        mockMvc.perform(put("/api/reviews/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REVIEW_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateReview_shouldReturnBadRequest_whenServiceThrowsIllegalArgument() throws Exception {
        when(reviewService.updateReview(eq(1L), any(ReviewUpdateDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(put("/api/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REVIEW_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteReview_shouldReturnNoContent_whenReviewExists() throws Exception {
        mockMvc.perform(delete("/api/reviews/1"))
                .andExpect(status().isNoContent());

        verify(reviewService, times(1)).deleteReview(1L);
    }

    @Test
    void deleteReview_shouldReturnNotFound_whenReviewDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Review not found: 999")).when(reviewService).deleteReview(999L);

        mockMvc.perform(delete("/api/reviews/999"))
                .andExpect(status().isNotFound());
    }
}
