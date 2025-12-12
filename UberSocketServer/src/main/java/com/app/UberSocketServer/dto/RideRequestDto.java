package com.app.UberSocketServer.dto;

import com.app.UberSocketServer.model.ExactLocation;
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
