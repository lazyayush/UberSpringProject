package com.app.UberReviewService.dto;

import lombok.Data;

@Data
public class CreateReviewDto {
    private String content;
    private Double rating;
    private Long bookingId;
}
