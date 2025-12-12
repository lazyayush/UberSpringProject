package com.app.UberBookingService.dto;

import com.app.UberEntityService.models.ExactLocation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RideRequestDto {
    private Long passengerId;

    private ExactLocation startLocation;
    private ExactLocation endLocation;

    private Long driverId;
    private Long bookingId;
}
