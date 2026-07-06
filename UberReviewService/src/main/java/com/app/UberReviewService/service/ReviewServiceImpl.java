package com.app.UberReviewService.service;

import com.app.UberEntityService.models.BookingStatus;
import com.app.UberEntityService.models.Driver;
import com.app.UberEntityService.models.Review;
import com.app.UberReviewService.exceptions.DuplicateReviewException;
import com.app.UberReviewService.exceptions.ReviewValidationException;
import com.app.UberReviewService.exceptions.UnauthorizedReviewException;
import com.app.UberReviewService.repositories.DriverRepository;
import com.app.UberReviewService.repositories.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final DriverRepository driverRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, DriverRepository driverRepository) {
        this.reviewRepository = reviewRepository;
        this.driverRepository = driverRepository;
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
    public Review publishReview(Review review, Long reviewerId) {
        var booking = review.getBooking();

        if(booking.getBookingStatus() != BookingStatus.COMPLETED) {
            throw new ReviewValidationException("Booking must be completed before it can be reviewed");
        }

        if (!booking.getPassenger().getId().equals(reviewerId)) {
            throw new UnauthorizedReviewException("Only the passenger on this booking can submit a review");
        }

        if (reviewRepository.findReviewByBookingId(booking.getId()).isPresent()) {
            throw new DuplicateReviewException("A review already exists for booking " + booking.getId());
        }

        Review savedReview = reviewRepository.save(review);

        Driver driver = booking.getDriver();
        int prevCount = driver.getNumRatings() == null ? 0 : driver.getNumRatings();
        double prevRating = driver.getRating() == null ? 0 : driver.getRating();

        double newRating = ((prevRating * prevCount) + savedReview.getRating()) / (prevCount + 1);
        driver.setRating(newRating);
        driver.setNumRatings(prevCount + 1);
        driverRepository.save(driver);

        return savedReview;
    }
}
