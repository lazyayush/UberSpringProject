package com.app.UberSocketServer.controller;

import com.app.UberSocketServer.dto.RideRequestDto;
import com.app.UberSocketServer.dto.RideResponseDto;
import com.app.UberSocketServer.service.RideResponseProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/api/socket")
public class DriverRequestController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RideResponseProcessor rideResponseProcessor;

    public DriverRequestController(SimpMessagingTemplate simpMessagingTemplate, RideResponseProcessor rideResponseProcessor) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.rideResponseProcessor = rideResponseProcessor;
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
    public void rideRequestHandler(@DestinationVariable String driverId, RideResponseDto responseDto) {
        rideResponseProcessor.process(driverId, responseDto);
    }
}
