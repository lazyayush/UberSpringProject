package com.app.UberBookingService.apis;


import com.app.UberBookingService.dto.DriverLocationDto;
import com.app.UberBookingService.dto.NearbyDriversRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "UberLocationService")
public interface LocationServiceApi {

    @PostMapping("/api/location/nearby/drivers")
    List<DriverLocationDto> getNearbyDrivers(@RequestBody NearbyDriversRequestDto requestDto);
}
