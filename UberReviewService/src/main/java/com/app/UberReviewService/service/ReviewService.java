package com.app.UberReviewService.service;

import com.app.UberReviewService.models.Review;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    public Optional<Review> findReviewById(Long id);
    public List<Review> findAllReviews();
    public boolean deleteReviewById(Long id);
    public Review publishReview(Review review);
    public Review updateReview(Long id, Review review);
}
