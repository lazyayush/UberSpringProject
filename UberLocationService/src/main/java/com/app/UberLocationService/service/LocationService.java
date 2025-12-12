package com.app.UberLocationService.service;

import com.app.UberLocationService.dto.DriverLocationDto;

import java.util.List;

public interface LocationService {
    Boolean saveDriverLocation(String driverId, Double latitude, Double longitude);
    List<DriverLocationDto> nearbyDrivers(Double latitude, Double longitude);
}
