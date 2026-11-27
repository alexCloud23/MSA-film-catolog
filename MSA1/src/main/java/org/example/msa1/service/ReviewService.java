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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;
    private final ReferenceMapper referenceMapper;

    public ReviewService(ReviewRepository reviewRepository, FilmRepository filmRepository, 
                        UserRepository userRepository, ReviewMapper reviewMapper, 
                        ReferenceMapper referenceMapper) {
        this.reviewRepository = reviewRepository;
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
        this.reviewMapper = reviewMapper;
        this.referenceMapper = referenceMapper;
    }

    public List<ReviewDTO> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    public List<ReviewDTO> getReviewsByFilmId(Long filmId) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new ResourceNotFoundException("Film not found: " + filmId));
        return reviewRepository.findByFilmId(film.getId()).stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    public ReviewDTO getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + id));
        return reviewMapper.toDto(review);
    }

    public ReviewDTO createReview(Long filmId, ReviewCreateDTO dto) {
        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new ResourceNotFoundException("Film not found: " + filmId));
        
        User author = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getAuthorId()));

        Review review = reviewMapper.toEntity(dto);
        review.setFilm(film);
        review.setAuthor(author);

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toDto(savedReview);
    }

    public ReviewDTO updateReview(Long id, ReviewUpdateDTO dto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + id));

        reviewMapper.updateEntityFromDto(dto, review);

        Review updatedReview = reviewRepository.saveAndFlush(review);
        return reviewMapper.toDto(updatedReview);
    }

    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review not found: " + id);
        }
        reviewRepository.deleteById(id);
    }
}