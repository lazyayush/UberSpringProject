package com.app.UberEntityService.models;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Random;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OTP extends BaseModel{
    private String code;
    private String sendToNumber;
    private static OTP make(String phoneNumber){
        Random random = new Random();

        //4 digit otp
        int code = random.nextInt(9000) + 1000;
        return OTP.builder().code(Integer.toString(code)).sendToNumber(phoneNumber).build();
    }
}
