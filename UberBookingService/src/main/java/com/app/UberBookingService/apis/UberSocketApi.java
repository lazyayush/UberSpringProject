package com.app.UberBookingService.apis;

import com.app.UberBookingService.dto.DriverLocationDto;
import com.app.UberBookingService.dto.NearbyDriversRequestDto;
import com.app.UberBookingService.dto.RideRequestDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UberSocketApi {
    @POST("/api/socket/newride")
    Call<Boolean> raiseRideRequest(@Body RideRequestDto requestDto);
}
