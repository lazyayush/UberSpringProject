package com.app.UberReviewService.exceptions;

public class ReviewValidationException extends RuntimeException {
    public ReviewValidationException(String message) { super(message); }
}