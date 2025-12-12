package com.app.UberAuthService.dto;

import com.app.UberEntityService.models.Booking;
import com.app.UberEntityService.models.Color;
import com.app.UberEntityService.models.Passenger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PassengerDto {
    private String id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private Date createdAt;

    public static PassengerDto from(Passenger p){
        return PassengerDto.builder()
                .id(p.getId().toString())
                .name(p.getName())
                .phoneNumber(p.getPhoneNumber())
                .password(p.getPassword())
                .email(p.getEmail())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
