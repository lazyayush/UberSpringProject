package com.app.UberSocketServer.apis;

import com.app.UberSocketServer.dto.UpdateBookingRequestDto;
import com.app.UberSocketServer.dto.UpdateBookingResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "UberBookingService")
public interface BookingServiceApi {

    @PostMapping("/api/v1/booking/{bookingId}")
    UpdateBookingResponseDto updateBooking(
            @PathVariable Long bookingId,
            @RequestBody UpdateBookingRequestDto requestDto
    );
}
