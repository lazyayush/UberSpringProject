package com.app.UberAuthService.dto;

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
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private Date createdAt;

    public static PassengerDto from(Passenger p){
        return PassengerDto.builder()
                .id(p.getId())
                .name(p.getName())
                .phoneNumber(p.getPhoneNumber())
                .email(p.getEmail())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
