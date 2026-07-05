package com.app.UberBookingService.exceptions;

public class BookingAlreadyAssignedException extends RuntimeException {
    public BookingAlreadyAssignedException(String message) {
        super(message);
    }
}