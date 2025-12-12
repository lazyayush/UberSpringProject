package com.app.UberBookingService.service;

import com.app.UberBookingService.apis.LocationServiceApi;
import com.app.UberBookingService.apis.UberSocketApi;
import com.app.UberBookingService.dto.*;
import com.app.UberBookingService.repositories.BookingRepository;
import com.app.UberBookingService.repositories.DriverRepository;
import com.app.UberBookingService.repositories.PassengerRepository;
import com.app.UberEntityService.models.*;
import jakarta.ws.rs.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class BookingServiceImpl implements BookingService{

    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;
    private final RestTemplate restTemplate;
//    private static final String LOCATION_SERVICE = "http://localhost:7477";
    private final LocationServiceApi locationServiceApi;
    private final UberSocketApi uberSocketApi;
    private final DriverRepository driverRepository;

    public BookingServiceImpl(PassengerRepository passengerRepository, BookingRepository bookingRepository, RestTemplate restTemplate, LocationServiceApi locationServiceApi, UberSocketApi uberSocketApi, DriverRepository driverRepository) {
        this.passengerRepository = passengerRepository;
        this.bookingRepository = bookingRepository;
        this.restTemplate = restTemplate;
        this.locationServiceApi = locationServiceApi;
        this.uberSocketApi = uberSocketApi;
        this.driverRepository = driverRepository;
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
        processNearbyDriversAsync(request, bookingDto.getPassengerId(), newBooking.getId());

        //api call to location service to fetch nearby drivers
//        ResponseEntity<DriverLocationDto[]> result = restTemplate.postForEntity(LOCATION_SERVICE + "/api/location/nearby/drivers", request, DriverLocationDto[].class);
//
//        if(result.getStatusCode().is2xxSuccessful() && result.getBody() != null){
//            List<DriverLocationDto> nearbyDrivers = Arrays.asList(result.getBody());
//            nearbyDrivers.forEach(driverLocationDto -> {
//                System.out.println(driverLocationDto.getDriverId() + " " + "latitude: " + driverLocationDto.getLatitude() + " , " + "longitude: " + driverLocationDto.getLongitude());
//            });
//        }

        return CreateBookingResponseDto.builder()
                .bookingId(newBooking.getId())
                .fare(newBooking.getFare())
                .bookingStatus(newBooking.getBookingStatus().toString())
                .driver(Optional.ofNullable(newBooking.getDriver()))
                .build();
    }

    @Override
    public UpdateBookingResponseDto updateBooking(UpdateBookingRequestDto updateBookingDto, Long bookingId) {
        Optional<Driver> driver = driverRepository.findById(updateBookingDto.getDriverId().get());
        bookingRepository.updateBookingStatusAndDriverById(bookingId, BookingStatus.SCHEDULED, driver.get());
        Optional<Booking> booking = bookingRepository.findById(bookingId);

        Random random = new Random();
        int code = random.nextInt(9000) + 1000;
        String otp = String.valueOf(code);

        return UpdateBookingResponseDto.builder()
                .bookingId(bookingId)
                .otp(otp)
                .bookingStatus(booking.get().getBookingStatus())
                .driver(Optional.ofNullable(booking.get().getDriver()))
                .build();
    }

    private void processNearbyDriversAsync(NearbyDriversRequestDto nearbyDriversRequestDto, Long passengerId, Long bookingId){
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()->new NotFoundException("Booking not found"));
        Call<DriverLocationDto[]> call = locationServiceApi.getNearbyDrivers(nearbyDriversRequestDto);
        call.enqueue(new Callback<DriverLocationDto[]>() {
            @Override
            public void onResponse(Call<DriverLocationDto[]> call, Response<DriverLocationDto[]> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<DriverLocationDto> nearbyDrivers = Arrays.asList(response.body());

                    System.out.println("--------Nearby drivers---------------");
                    nearbyDrivers.forEach(driverLocationDto -> {
                        System.out.println(driverLocationDto.getDriverId() + " " + "latitude: " + driverLocationDto.getLatitude() + " , " + "longitude: " + driverLocationDto.getLongitude());
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
                            raiseRideRequestAsync(rideRequest);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    System.out.println("Request failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<DriverLocationDto[]> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private void raiseRideRequestAsync(RideRequestDto requestDto){
        Call<Boolean> call = uberSocketApi.raiseRideRequest(requestDto);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.isSuccessful() && response.body() != null){
                    Boolean result = response.body();
                    System.out.println("Driver ride request sent: " + result.toString());
                } else {
                    System.out.println("Request for ride failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}
