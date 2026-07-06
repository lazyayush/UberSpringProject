package com.app.UberReviewService.adapter;

import com.app.UberEntityService.models.Review;
import com.app.UberReviewService.dto.CreateReviewDto;

public interface CreateReviewDtoToReviewDto {
    Review convertDto(CreateReviewDto createReviewDto);
}
