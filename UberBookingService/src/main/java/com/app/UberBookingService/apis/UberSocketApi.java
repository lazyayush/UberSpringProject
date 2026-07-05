package com.app.UberBookingService.apis;

import com.app.UberBookingService.dto.DriverLocationDto;
import com.app.UberBookingService.dto.NearbyDriversRequestDto;
import com.app.UberBookingService.dto.RideRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "UberSocketServer")
public interface UberSocketApi {
    @PostMapping("/api/socket/newride")
    Boolean raiseRideRequest(@RequestBody RideRequestDto requestDto);
}
