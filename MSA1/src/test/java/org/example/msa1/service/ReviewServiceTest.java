package org.example.msa1.service;

import org.example.msa1.dto.ReviewCreateDTO;
import org.example.msa1.dto.ReviewDTO;
import org.example.msa1.dto.ReviewUpdateDTO;
import org.example.msa1.exception.ResourceNotFoundException;
import org.example.msa1.mapper.ReferenceMapper;
import org.example.msa1.mapper.ReviewMapper;
import org.example.msa1.model.Film;
import org.example.msa1.model.Review;
import org.example.msa1.model.User;
import org.example.msa1.repository.FilmRepository;
import org.example.msa1.repository.ReviewRepository;
import org.example.msa1.repository.UserRepository;
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
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private ReferenceMapper referenceMapper;

    @InjectMocks
    private ReviewService reviewService;

    private Review review;
    private ReviewDTO reviewDTO;
    private Film film;
    private User user;

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setId(1L);
        film.setTitle("Интерстеллар");
        film.setSlug("interstellar");

        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        review = new Review();
        review.setId(1L);
        review.setFilm(film);
        review.setAuthor(user);
        review.setText("Отличный фильм!");
        review.setRating(9);
        review.setCreatedAt(LocalDate.now());
        review.setUpdatedAt(LocalDate.now());

        reviewDTO = new ReviewDTO();
        reviewDTO.setId(1L);
        reviewDTO.setFilmId(1L);
        reviewDTO.setAuthorId(1L);
        reviewDTO.setText("Отличный фильм!");
        reviewDTO.setRating(9);
        reviewDTO.setCreatedAt(LocalDate.now());
    }

    @Test
    void getAllReviews_shouldReturnAllReviews_whenRepositoryHasReviews() {
        when(reviewRepository.findAll()).thenReturn(List.of(review));
        when(reviewMapper.toDto(review)).thenReturn(reviewDTO);

        List<ReviewDTO> result = reviewService.getAllReviews();

        assertEquals(1, result.size());
        assertEquals(reviewDTO, result.get(0));
        verify(reviewRepository, times(1)).findAll();
        verify(reviewMapper, times(1)).toDto(review);
    }

    @Test
    void getReviewById_shouldReturnReview_whenReviewExists() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewMapper.toDto(review)).thenReturn(reviewDTO);

        ReviewDTO result = reviewService.getReviewById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Отличный фильм!", result.getText());
        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewMapper, times(1)).toDto(review);
    }

    @Test
    void getReviewById_shouldThrowException_whenReviewDoesNotExist() {
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.getReviewById(999L));

        assertTrue(exception.getMessage().contains("Review not found: 999"));
        verify(reviewRepository, times(1)).findById(999L);
        verify(reviewMapper, never()).toDto(any());
    }

    @Test
    void getReviewsByFilmId_shouldReturnReviews_whenFilmExists() {
        when(filmRepository.findById(1L)).thenReturn(Optional.of(film));
        when(reviewRepository.findByFilmId(1L)).thenReturn(List.of(review));
        when(reviewMapper.toDto(review)).thenReturn(reviewDTO);

        List<ReviewDTO> result = reviewService.getReviewsByFilmId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(filmRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).findByFilmId(1L);
        verify(reviewMapper, times(1)).toDto(review);
    }

    @Test
    void getReviewsByFilmId_shouldThrowException_whenFilmDoesNotExist() {
        when(filmRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.getReviewsByFilmId(999L));

        assertTrue(exception.getMessage().contains("Film not found: 999"));
        verify(filmRepository, times(1)).findById(999L);
        verify(reviewRepository, never()).findByFilmId(anyLong());
    }

    @Test
    void createReview_shouldCreateReview_whenFilmAndUserExist() {
        ReviewCreateDTO createDTO = new ReviewCreateDTO();
        createDTO.setAuthorId(1L);
        createDTO.setText("Отличный фильм!");
        createDTO.setRating(9);

        when(filmRepository.findById(1L)).thenReturn(Optional.of(film));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewMapper.toEntity(createDTO)).thenReturn(review);
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toDto(review)).thenReturn(reviewDTO);

        ReviewDTO result = reviewService.createReview(1L, createDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Отличный фильм!", result.getText());
        assertEquals(9, result.getRating());

        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository, times(1)).save(reviewCaptor.capture());
        Review capturedReview = reviewCaptor.getValue();
        assertEquals(film, capturedReview.getFilm());
        assertEquals(user, capturedReview.getAuthor());
        assertNotNull(capturedReview.getCreatedAt());
        assertNotNull(capturedReview.getUpdatedAt());
    }

    @Test
    void createReview_shouldThrowException_whenFilmDoesNotExist() {
        ReviewCreateDTO createDTO = new ReviewCreateDTO();
        createDTO.setAuthorId(1L);
        createDTO.setText("Текст отзыва");
        createDTO.setRating(8);

        when(filmRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.createReview(999L, createDTO));

        assertTrue(exception.getMessage().contains("Film not found: 999"));
        verify(filmRepository, times(1)).findById(999L);
        verify(userRepository, never()).findById(anyLong());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void createReview_shouldThrowException_whenUserDoesNotExist() {
        ReviewCreateDTO createDTO = new ReviewCreateDTO();
        createDTO.setAuthorId(999L);
        createDTO.setText("Текст отзыва");
        createDTO.setRating(8);

        when(filmRepository.findById(1L)).thenReturn(Optional.of(film));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.createReview(1L, createDTO));

        assertTrue(exception.getMessage().contains("User not found: 999"));
        verify(filmRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(999L);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void updateReview_shouldUpdateReview_whenReviewExists() {
        ReviewUpdateDTO updateDTO = new ReviewUpdateDTO();
        updateDTO.setText("Обновлённый текст отзыва");
        updateDTO.setRating(10);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.saveAndFlush(review)).thenReturn(review);

        ReviewDTO updatedReviewDTO = new ReviewDTO();
        updatedReviewDTO.setId(1L);
        updatedReviewDTO.setFilmId(1L);
        updatedReviewDTO.setAuthorId(1L);
        updatedReviewDTO.setText("Обновлённый текст отзыва");
        updatedReviewDTO.setRating(10);
        updatedReviewDTO.setCreatedAt(LocalDate.now());

        when(reviewMapper.toDto(review)).thenReturn(updatedReviewDTO);

        ReviewDTO result = reviewService.updateReview(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Обновлённый текст отзыва", result.getText());
        assertEquals(10, result.getRating());
        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).saveAndFlush(review);
        verify(reviewMapper, times(1)).updateEntityFromDto(any(ReviewUpdateDTO.class), any(Review.class));
    }

    @Test
    void updateReview_shouldThrowException_whenReviewDoesNotExist() {
        ReviewUpdateDTO updateDTO = new ReviewUpdateDTO();
        updateDTO.setText("Новый текст");
        updateDTO.setRating(8);

        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.updateReview(999L, updateDTO));

        assertTrue(exception.getMessage().contains("Review not found: 999"));
        verify(reviewRepository, times(1)).findById(999L);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void deleteReview_shouldDeleteReview_whenReviewExists() {
        when(reviewRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reviewRepository).deleteById(1L);

        reviewService.deleteReview(1L);

        verify(reviewRepository, times(1)).existsById(1L);
        verify(reviewRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteReview_shouldThrowException_whenReviewDoesNotExist() {
        when(reviewRepository.existsById(999L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.deleteReview(999L));

        assertTrue(exception.getMessage().contains("Review not found: 999"));
        verify(reviewRepository, times(1)).existsById(999L);
        verify(reviewRepository, never()).deleteById(anyLong());
    }
}
