package com.app.UberReviewService.adapter;

import com.app.UberReviewService.dto.CreateReviewDto;
import com.app.UberReviewService.models.Review;

public interface CreateReviewDtoToReviewDto {
    Review convertDto(CreateReviewDto createReviewDto);
}
