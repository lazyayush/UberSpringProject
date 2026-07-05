package com.app.UberSocketServer.service;

import com.app.UberEntityService.models.BookingStatus;
import com.app.UberSocketServer.apis.BookingServiceApi;
import com.app.UberSocketServer.dto.RideResponseDto;
import com.app.UberSocketServer.dto.UpdateBookingRequestDto;
import feign.FeignException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class RideResponseProcessor {

    private final BookingServiceApi bookingServiceApi;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public RideResponseProcessor(BookingServiceApi bookingServiceApi, SimpMessagingTemplate simpMessagingTemplate) {
        this.bookingServiceApi = bookingServiceApi;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Async
    public void process(String driverId, RideResponseDto responseDto){
        UpdateBookingRequestDto dto = UpdateBookingRequestDto.builder()
                .driverId(Optional.of(Long.parseLong(driverId)))
                .bookingStatus(BookingStatus.SCHEDULED)
                .build();
        try{
            var result = bookingServiceApi.updateBooking(responseDto.getBookingId(), dto);
            System.out.println("Booking confirmed for driver " + driverId + ": " + result);
        } catch (FeignException.Conflict conflict) {
            System.out.println("Driver " + driverId + " lost the race for booking " + responseDto.getBookingId());
            simpMessagingTemplate.convertAndSend(
                    "/topic/rideRequest/" + driverId,
                    Map.of("bookingId", responseDto.getBookingId(), "status", "ALREADY_ACCEPTED")
            );
        } catch (FeignException e){
            System.out.println("Unexpected booking update error: " + e.status());
        }
    }
}
