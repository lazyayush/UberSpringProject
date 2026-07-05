package com.app.UberBookingService.service;

import com.app.UberBookingService.apis.LocationServiceApi;
import com.app.UberBookingService.apis.UberSocketApi;
import com.app.UberBookingService.dto.*;
import com.app.UberBookingService.exceptions.BookingAlreadyAssignedException;
import com.app.UberBookingService.repositories.BookingRepository;
import com.app.UberBookingService.repositories.DriverRepository;
import com.app.UberBookingService.repositories.PassengerRepository;
import com.app.UberEntityService.models.*;
import jakarta.ws.rs.NotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class BookingServiceImpl implements BookingService{

    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;
    private final LocationServiceApi locationServiceApi;
    private final UberSocketApi uberSocketApi;
    private final DriverRepository driverRepository;
    private final BookingService self;

    public BookingServiceImpl(PassengerRepository passengerRepository, BookingRepository bookingRepository,
                              LocationServiceApi locationServiceApi, UberSocketApi uberSocketApi,
                              DriverRepository driverRepository, @Lazy BookingService self) {
        this.passengerRepository = passengerRepository;
        this.bookingRepository = bookingRepository;
        this.locationServiceApi = locationServiceApi;
        this.uberSocketApi = uberSocketApi;
        this.driverRepository = driverRepository;
        this.self = self;
    }

    @Override
    public CreateBookingResponseDto createBooking(CreateBookingDto bookingDto) {
        bookingDto.calculateDistance();
        Passenger passenger = passengerRepository.findById(bookingDto.getPassengerId())
                .orElseThrow(() -> new RuntimeException("Passenger not found with id " + bookingDto.getPassengerId()));
        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.ASSIGNING_DRIVER)
                .distance(bookingDto.getDistance())
                .startLocation(bookingDto.getStartLocation())
                .endLocation(bookingDto.getEndLocation())
                .fare(bookingDto.getDistance() * 12)
                .passenger(passenger).build();
        Booking newBooking = bookingRepository.save(booking);

        NearbyDriversRequestDto request = NearbyDriversRequestDto.builder()
                .latitude(bookingDto.getStartLocation().getLatitude())
                .longitude(bookingDto.getStartLocation().getLongitude())
                .build();

        //fetching nearby drivers within 5km range
        self.processNearbyDriversAsync(request, bookingDto.getPassengerId(), newBooking.getId());

        return CreateBookingResponseDto.builder()
                .bookingId(newBooking.getId())
                .fare(newBooking.getFare())
                .bookingStatus(newBooking.getBookingStatus().toString())
                .driver(Optional.ofNullable(newBooking.getDriver()))
                .build();
    }

    @Override
    public UpdateBookingResponseDto updateBooking(UpdateBookingRequestDto updateBookingDto, Long bookingId) {
        Driver driver = driverRepository.findById(updateBookingDto.getDriverId().get())
                .orElseThrow(() -> new NotFoundException("Driver not found"));

        int rowsUpdated = bookingRepository.updateBookingStatusAndDriverById(
                bookingId, BookingStatus.SCHEDULED, driver, BookingStatus.ASSIGNING_DRIVER
        );

        if(rowsUpdated == 0){
            throw new BookingAlreadyAssignedException(
                    "Booking " + bookingId + " has already been accepted by another driver"
            );
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        Random random = new Random();
        int code = random.nextInt(9000) + 1000;
        String otp = String.valueOf(code);

        return UpdateBookingResponseDto.builder()
                .bookingId(bookingId)
                .otp(otp)
                .bookingStatus(booking.getBookingStatus())
                .driver(Optional.ofNullable(booking.getDriver()))
                .build();
    }

    @Override
    @Async("taskExecutor")
    public void processNearbyDriversAsync(NearbyDriversRequestDto nearbyDriversRequestDto, Long passengerId, Long bookingId){
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()->new NotFoundException("Booking not found"));

        List<DriverLocationDto> nearbyDrivers = locationServiceApi.getNearbyDrivers(nearbyDriversRequestDto);

        System.out.println("--------Nearby drivers---------------");
        nearbyDrivers.forEach(d -> {
            System.out.println(d.getDriverId() + " " + "latitude: " + d.getLatitude() + " , " + "longitude: " + d.getLongitude());
        });
        System.out.println("--------------------------------------");

        //sending ride request to nearby drivers only
        for(DriverLocationDto driverLocationDto : nearbyDrivers){
            RideRequestDto rideRequest = RideRequestDto.builder()
                    .passengerId(passengerId)
                    .bookingId(bookingId)
                    .driverId(Long.valueOf(driverLocationDto.getDriverId()))
                    .startLocation(booking.getStartLocation())
                    .endLocation(booking.getEndLocation())
                    .build();

            try{
                uberSocketApi.raiseRideRequest(rideRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
