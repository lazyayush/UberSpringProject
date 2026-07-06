package com.app.UberReviewService.controller;

import com.app.UberEntityService.models.Review;
import com.app.UberReviewService.adapter.CreateReviewDtoToReviewDto;
import com.app.UberReviewService.dto.CreateReviewDto;
import com.app.UberReviewService.dto.ReviewDto;
import com.app.UberReviewService.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final CreateReviewDtoToReviewDto createReviewDtoToReviewDto;

    public ReviewController(ReviewService reviewService, CreateReviewDtoToReviewDto createReviewDtoToReviewDto) {
        this.reviewService = reviewService;
        this.createReviewDtoToReviewDto = createReviewDtoToReviewDto;
    }

    @PostMapping
    public ResponseEntity<?> postReview(@Validated @RequestBody CreateReviewDto reviewDto,
                                        @RequestHeader("X-User-Id") Long reviewerId){
        Review incomingReview = this.createReviewDtoToReviewDto.convertDto(reviewDto);

        if(incomingReview == null){
            return new ResponseEntity<>("Invalid request!", HttpStatus.BAD_REQUEST);
        }

        Review review = reviewService.publishReview(incomingReview, reviewerId);

        ReviewDto response = ReviewDto.builder()
                .id(review.getId())
                .content(review.getContent())
                .bookingId(review.getBooking().getId())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAllReviews(){
        List<ReviewDto> reviews = reviewService.findAllReviews()
                .stream()
                .map(r -> ReviewDto.builder()
                        .id(r.getId())
                        .content(r.getContent())
                        .bookingId(r.getBooking().getId())
                        .rating(r.getRating())
                        .createdAt(r.getCreatedAt())
                        .updatedAt(r.getUpdatedAt())
                        .build())
                .toList();
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<?> findReviewById(@PathVariable Long reviewId){
        Review review = reviewService.findReviewById(reviewId).orElseThrow();
        ReviewDto response = ReviewDto.builder()
                .id(review.getId())
                .content(review.getContent())
                .bookingId(review.getBooking().getId())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReviewById(@PathVariable Long reviewId){
        try{
            boolean isDeleted = reviewService.deleteReviewById(reviewId);
            if(!isDeleted){
                return new ResponseEntity<>("Unable to delete review", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>("Review deleted!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
