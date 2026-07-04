package com.app.UberLocationService.service;

import com.app.UberLocationService.dto.DriverLocationDto;
import jakarta.annotation.PostConstruct;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationServiceImpl implements LocationService{

    private final StringRedisTemplate stringRedisTemplate;
    private static final String DRIVER_GEO_OPS_KEY = "driver";
    private final static Double SEARCH_RADIUS = 5.0;

    public LocationServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    public Boolean saveDriverLocation(String driverId, Double latitude, Double longitude) {
            GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
            geoOps.add(DRIVER_GEO_OPS_KEY, new RedisGeoCommands.GeoLocation<>(
                    driverId,
                    new Point(
                            longitude,
                            latitude)
            ));
            return true;
    }

    @Override
    public List<DriverLocationDto> nearbyDrivers(Double latitude, Double longitude) {
            GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
            Distance radius = new Distance(SEARCH_RADIUS, Metrics.KILOMETERS);
            Circle within = new Circle(new Point(
                    longitude,
                    latitude),
                    radius);
            GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOps.radius(DRIVER_GEO_OPS_KEY, within);
            List<DriverLocationDto> drivers = new ArrayList<>();
            for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : results) {
                Point point = geoOps.position(DRIVER_GEO_OPS_KEY, result.getContent().getName()).get(0);
                DriverLocationDto driverLocation = DriverLocationDto.builder()
                        .driverId(result.getContent().getName())
                        .longitude(point.getX())
                        .latitude(point.getY())
                        .build();
                drivers.add(driverLocation);
            }
            return drivers;
    }
}
