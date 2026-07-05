package com.app.UberAuthService.controller;

import com.app.UberAuthService.dto.*;
import com.app.UberAuthService.repository.PassengerRepository;
import com.app.UberAuthService.service.AuthService;
import com.app.UberAuthService.service.JwtService;
import com.app.UberEntityService.models.Passenger;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PassengerRepository passengerRepository;

    public AuthController(AuthService authService, JwtService jwtService, AuthenticationManager authenticationManager, PassengerRepository passengerRepository) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passengerRepository = passengerRepository;
    }

    @PostMapping("/signup/passenger")
    public ResponseEntity<?> signupPassenger(@RequestBody PassengerSignupRequestDto dto){
        PassengerDto response = authService.signupPassenger(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/signIn/passenger")
    public ResponseEntity<?> singInPassenger(@RequestBody AuthRequestDto request, HttpServletResponse response){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        if(authentication.isAuthenticated()){
            Passenger passenger = passengerRepository.findPassengerByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

            String jwtToken = jwtService.generateToken(passenger.getId(), "PASSENGER", request.getEmail());

            ResponseCookie cookie = ResponseCookie.from("JwtToken", jwtToken)
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(7*24*3600)
                    .build();

            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return new ResponseEntity<>(AuthResponseDto
                    .builder().success(true).token(jwtToken).build(),
                    HttpStatus.OK);
        } else {
            throw new UsernameNotFoundException("User not found!");
        }
    }

    @PostMapping("/signup/driver")
    public ResponseEntity<?> signupDriver(@RequestBody DriverSignupDto driver){
        DriverDto response = authService.signupDriver(driver);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
