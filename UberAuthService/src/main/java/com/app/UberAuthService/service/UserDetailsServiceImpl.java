package com.app.UberAuthService.service;

import com.app.UberAuthService.helper.AuthPassengerDetails;
import com.app.UberEntityService.models.Passenger;
import com.app.UberAuthService.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PassengerRepository passengerRepository;

//    public UserDetailsServiceImpl(PassengerRepository passengerRepository) {
//        this.passengerRepository = passengerRepository;
//    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Passenger> passenger = passengerRepository.findPassengerByEmail(email);
        if(passenger.isPresent()){
            return new AuthPassengerDetails(passenger.get());
        } else {
            throw new UsernameNotFoundException("Passenger with the given email not found!");
        }
    }
}
