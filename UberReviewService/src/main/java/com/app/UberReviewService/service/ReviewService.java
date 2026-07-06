package com.app.UberReviewService.service;

import com.app.UberEntityService.models.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Optional<Review> findReviewById(Long id);
    List<Review> findAllReviews();
    boolean deleteReviewById(Long id);
    Review publishReview(Review review, Long reviewerId);
}
