package com.app.UberBookingService.controller;

import com.app.UberBookingService.dto.CreateBookingDto;
import com.app.UberBookingService.dto.CreateBookingResponseDto;
import com.app.UberBookingService.dto.UpdateBookingRequestDto;
import com.app.UberBookingService.dto.UpdateBookingResponseDto;
import com.app.UberBookingService.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }


    @PostMapping
    public ResponseEntity<CreateBookingResponseDto> createBooking(@RequestBody CreateBookingDto bookingDto,
                                                                  @RequestHeader("X-User-Id") Long passengerId){
        bookingDto.setPassengerId(passengerId);
        return new ResponseEntity<>(bookingService.createBooking(bookingDto), HttpStatus.CREATED);
    }

    @PostMapping("/{bookingId}")
    public ResponseEntity<UpdateBookingResponseDto> updateBooking(@RequestBody UpdateBookingRequestDto updateBooking, @PathVariable Long bookingId){
        return new ResponseEntity<>(bookingService.updateBooking(updateBooking, bookingId), HttpStatus.OK);
    }

    @PostMapping("/{bookingId}/complete")
    public ResponseEntity<Void> completeBooking(@PathVariable Long bookingId){
        bookingService.completeBooking(bookingId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
