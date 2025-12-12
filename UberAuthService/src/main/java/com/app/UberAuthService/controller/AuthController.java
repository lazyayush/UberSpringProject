package com.app.UberAuthService.controller;

import com.app.UberAuthService.dto.*;
import com.app.UberAuthService.service.AuthService;
import com.app.UberAuthService.service.JwtService;
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

    public AuthController(AuthService authService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signup/passenger")
    public ResponseEntity<?> signupPassenger(@RequestBody PassengerSignupRequestDto dto){
        PassengerDto response = authService.signupPassenger(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/signIn/passenger")
    public ResponseEntity<?> singInPassenger(@RequestBody AuthRequestDto request, HttpServletResponse response){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        if(authentication.isAuthenticated()){
            String jwtToken = jwtService.generateToken(request.getEmail());

            ResponseCookie cookie = ResponseCookie.from("JwtToken", jwtToken)
                    .httpOnly(true)
                    .secure(false)
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

    //to validate incoming request from other microservices
    @GetMapping("/validate")
    public ResponseEntity<?> validate(HttpServletRequest request, HttpServletResponse response){
        for(Cookie cookie : request.getCookies()){
            System.out.println(cookie.getName() + " " + cookie.getValue());
        }
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

}
