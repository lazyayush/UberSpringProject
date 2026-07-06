package com.app.UberReviewService.repositories;

import com.app.UberEntityService.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select r from Review r where r.booking.id = :bookingId")
    Optional<Review> findReviewByBookingId(@Param("bookingId") Long bookingId);
}