package com.app.UberBookingService.dto;

import com.app.UberEntityService.models.Driver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBookingResponseDto {

    private Long bookingId;
    private Long fare;
    private String bookingStatus;
    private Optional<Driver> driver;
}
