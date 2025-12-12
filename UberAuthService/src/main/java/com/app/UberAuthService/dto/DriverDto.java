package com.app.UberAuthService.dto;

import com.app.UberEntityService.models.Driver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriverDto {
    private String id;
    private String name;
    private String licenseNumber;
    private String phoneNumber;
    private String email;
    private String password;
    private Date createdAt;

    public static DriverDto from(Driver d){
        return DriverDto.builder()
                .id(d.getId().toString())
                .name(d.getName())
                .email(d.getEmail())
                .password(d.getPassword())
                .licenseNumber(d.getLicenseNumber())
                .phoneNumber(d.getPhoneNumber())
                .createdAt(d.getCreatedAt())
                .build();
    }

}
