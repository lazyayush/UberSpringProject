package com.app.UberSocketServer.controller;

import com.app.UberEntityService.models.BookingStatus;
import com.app.UberSocketServer.dto.RideRequestDto;
import com.app.UberSocketServer.dto.RideResponseDto;
import com.app.UberSocketServer.dto.UpdateBookingRequestDto;
import com.app.UberSocketServer.dto.UpdateBookingResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Controller
@RequestMapping("/api/socket")
public class DriverRequestController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RestTemplate restTemplate;

    public DriverRequestController(SimpMessagingTemplate simpMessagingTemplate, RestTemplate restTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/newride")
    @CrossOrigin(originPatterns = "*")
    public ResponseEntity<?> raiseRideRequest(@RequestBody RideRequestDto requestDto){
        sendDriverNewRideRequest(requestDto);
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    public void sendDriverNewRideRequest(RideRequestDto requestDto){
        if(requestDto.getDriverId() != null){
            simpMessagingTemplate.convertAndSend("/topic/rideRequest/" + requestDto.getDriverId(), requestDto);
            System.out.println("Ride request sent to driver: " + requestDto.getDriverId());
        } else {
            System.out.println("Driver id is missing in the ride request");
        }
    }

    @MessageMapping("/rideResponse/{driverId}")
    public synchronized void rideRequestHandler(@DestinationVariable String driverId, RideResponseDto responseDto){

        System.out.println(responseDto.getResponse() + " driverId: " + driverId);
        UpdateBookingRequestDto dto = UpdateBookingRequestDto.builder()
                .driverId(Optional.of(Long.parseLong(driverId)))
                .bookingStatus(BookingStatus.SCHEDULED)
                .build();
        ResponseEntity<UpdateBookingResponseDto> result = this.restTemplate.postForEntity("http://localhost:7478/api/v1/booking/" + responseDto.bookingId, dto, UpdateBookingResponseDto.class);

        System.out.println(result.getBody().toString());
        System.out.println(result.getStatusCode());

    }
}
