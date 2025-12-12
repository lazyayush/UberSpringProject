package com.app.UberSocketServer.dto;

import com.app.UberEntityService.models.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBookingRequestDto {

    private BookingStatus bookingStatus;
    private Optional<Long> driverId;
}
