package com.app.UberReviewService.exceptions;

public class UnauthorizedReviewException extends RuntimeException {
    public UnauthorizedReviewException(String message) { super(message); }
}