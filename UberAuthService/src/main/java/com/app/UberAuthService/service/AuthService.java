package com.app.UberAuthService.service;

import com.app.UberAuthService.dto.DriverDto;
import com.app.UberAuthService.dto.DriverSignupDto;
import com.app.UberAuthService.dto.PassengerDto;
import com.app.UberAuthService.dto.PassengerSignupRequestDto;
import com.app.UberEntityService.models.Driver;
import com.app.UberEntityService.models.Passenger;
import com.app.UberAuthService.repository.DriverRepository;
import com.app.UberAuthService.repository.PassengerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PassengerRepository passengerRepository;
    private final DriverRepository driverRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(PassengerRepository passengerRepository, DriverRepository driverRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.passengerRepository = passengerRepository;
        this.driverRepository = driverRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public PassengerDto signupPassenger(PassengerSignupRequestDto dto) {
        Passenger passenger = Passenger.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .phoneNumber(dto.getPhoneNumber())
                .build();

        Passenger savedPassenger = passengerRepository.save(passenger);
        return PassengerDto.from(savedPassenger);
    }

    public DriverDto signupDriver(DriverSignupDto dto){
        Driver driver = Driver.builder()
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .licenseNumber(dto.getLicenseNumber())
                .phoneNumber(dto.getPhoneNumber())
                .build();

        Driver savedDriver = driverRepository.save(driver);
        return DriverDto.from(savedDriver);
    }
}
