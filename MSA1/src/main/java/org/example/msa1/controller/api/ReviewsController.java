package org.example.msa1.controller.api;

import org.example.msa1.dto.ReviewCreateDTO;
import org.example.msa1.dto.ReviewDTO;
import org.example.msa1.dto.ReviewUpdateDTO;
import org.example.msa1.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewsController {

    private final ReviewService reviewService;

    public ReviewsController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        List<ReviewDTO> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long id) {
        ReviewDTO review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/film/{filmId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByFilmId(@PathVariable Long filmId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByFilmId(filmId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/film/{filmId}")
    public ResponseEntity<ReviewDTO> createReview(@PathVariable Long filmId, @Valid @RequestBody ReviewCreateDTO dto) {
        ReviewDTO createdReview = reviewService.createReview(filmId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long id, @Valid @RequestBody ReviewUpdateDTO dto) {
        ReviewDTO updatedReview = reviewService.updateReview(id, dto);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}