package com.app.UberReviewService.exceptions;

public class DuplicateReviewException extends RuntimeException {
    public DuplicateReviewException(String message) { super(message); }
}