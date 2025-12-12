package com.app.UberBookingService.service;

import com.app.UberBookingService.dto.CreateBookingDto;
import com.app.UberBookingService.dto.CreateBookingResponseDto;
import com.app.UberBookingService.dto.UpdateBookingRequestDto;
import com.app.UberBookingService.dto.UpdateBookingResponseDto;
import com.app.UberEntityService.models.Booking;

public interface BookingService {
    CreateBookingResponseDto createBooking(CreateBookingDto bookingDto);
    UpdateBookingResponseDto updateBooking(UpdateBookingRequestDto updateBookingDto, Long bookingId);
}
