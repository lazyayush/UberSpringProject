package com.app.UberReviewService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDto {

    private Long id;
    private String content;
    private Double rating;
    private Long bookingId;
    private Date createdAt;
    private Date updatedAt;
}
