package com.app.UberBookingService.service;

import com.app.UberBookingService.dto.*;
import com.app.UberEntityService.models.Booking;

public interface BookingService {
    CreateBookingResponseDto createBooking(CreateBookingDto bookingDto);
    UpdateBookingResponseDto updateBooking(UpdateBookingRequestDto updateBookingDto, Long bookingId);
    void processNearbyDriversAsync(NearbyDriversRequestDto nearbyDriversRequestDto, Long passengerId, Long bookingId);
}
