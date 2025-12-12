package com.app.UberAuthService.dto;

import com.app.UberEntityService.models.Car;
import com.app.UberEntityService.models.Driver;
import com.app.UberEntityService.models.OTP;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDto {
    private String email;
    private String password;
}
