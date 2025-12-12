package com.app.UberBookingService.dto;

import com.app.UberEntityService.models.BookingStatus;
import com.app.UberEntityService.models.Driver;
import com.app.UberEntityService.models.OTP;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBookingResponseDto {

    private Long bookingId;
    private BookingStatus bookingStatus;
    private Optional<Driver> driver;
    private String otp;
}
