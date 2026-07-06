package com.app.UberReviewService.adapter;

import com.app.UberEntityService.models.Booking;
import com.app.UberEntityService.models.Review;
import com.app.UberReviewService.dto.CreateReviewDto;
import com.app.UberReviewService.repositories.BookingRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CreateReviewDtoToReviewDtoImpl implements CreateReviewDtoToReviewDto{

    private final BookingRepository bookingRepository;

    public CreateReviewDtoToReviewDtoImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Review convertDto(CreateReviewDto dto) {
        Optional<Booking> booking = bookingRepository.findById(dto.getBookingId());
        return booking.map(val -> Review.builder()
                .rating(dto.getRating())
                .content(dto.getContent())
                .booking(val)
                .build()).orElse(null);
    }
}
