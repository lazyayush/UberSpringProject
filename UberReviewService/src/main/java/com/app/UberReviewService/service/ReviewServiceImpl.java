package com.app.UberReviewService.service;

import com.app.UberReviewService.models.Review;
import com.app.UberReviewService.repositories.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }


    @Override
    public Optional<Review> findReviewById(Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        if(review.isEmpty()){
            throw new EntityNotFoundException("Review with id: " + id + " not found!");
        }
        return review;
    }

    @Override
    public List<Review> findAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public boolean deleteReviewById(Long id) {
        try{
            Review review = reviewRepository.findById(id).orElseThrow(EntityNotFoundException::new);
            reviewRepository.delete(review);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    @Override
    @Transactional
    public Review publishReview(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(Long id, Review updatedReview) {
        Review review = reviewRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(updatedReview.getRating() != null){
            review.setRating(updatedReview.getRating());
        }
        if(updatedReview.getContent() != null){
            review.setContent(updatedReview.getContent());
        }
        return reviewRepository.save(review);
    }
}
