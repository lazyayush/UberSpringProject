package com.app.UberLocationService.service;

import com.app.UberEntityService.models.DriverApprovalStatus;
import com.app.UberLocationService.dto.DriverLocationDto;
import com.app.UberLocationService.repositories.DriverRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class LocationServiceImpl implements LocationService{

    private final StringRedisTemplate stringRedisTemplate;
    private final DriverRepository driverRepository;

    private static final String DRIVER_GEO_OPS_KEY = "driver";
    private static final String ONLINE_KEY_PREFIX = "driver:online:";
    private static final Double SEARCH_RADIUS = 5.0;
    private static final Duration ONLINE_TTL = Duration.ofSeconds(600);

    public LocationServiceImpl(StringRedisTemplate stringRedisTemplate, DriverRepository driverRepository) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.driverRepository = driverRepository;
    }


    @Override
    public Boolean saveDriverLocation(String driverId, Double latitude, Double longitude) {
        Long id = Long.parseLong(driverId);

        boolean isValidDriver = driverRepository
                .findByIdAndDriverApprovalStatus(id, DriverApprovalStatus.APPROVED)
                .isPresent();

        if(!isValidDriver) {
            return false;
        }

        GeoOperations<String, String> geoOps = stringRedisTemplate.opsForGeo();
        geoOps.add(DRIVER_GEO_OPS_KEY, new RedisGeoCommands.GeoLocation<>(
                driverId,
                new Point(
                        longitude,
                        latitude)
        ));

        stringRedisTemplate.opsForValue()
                .set(ONLINE_KEY_PREFIX + driverId, "1", ONLINE_TTL);

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
            String driverId = result.getContent().getName();
            Boolean isOnline = stringRedisTemplate.hasKey(ONLINE_KEY_PREFIX + driverId);

            if(!isOnline) {
                continue;
            }

            Point point = geoOps.position(DRIVER_GEO_OPS_KEY, driverId).get(0);
            DriverLocationDto driverLocation = DriverLocationDto.builder()
                    .driverId(driverId)
                    .longitude(point.getX())
                    .latitude(point.getY())
                    .build();
            drivers.add(driverLocation);
        }
        return drivers;
    }
}
