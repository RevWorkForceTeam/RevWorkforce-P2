package com.rev.revworkforcep2.service.performance;

import com.rev.revworkforcep2.dto.request.performance.CreateReviewRequest;
import com.rev.revworkforcep2.dto.response.performance.PerformanceReviewResponse;

import java.util.List;

public interface PerformanceReviewService {

    PerformanceReviewResponse createReview(CreateReviewRequest request);

    PerformanceReviewResponse getReviewById(Long id);

    List<PerformanceReviewResponse> getReviewsByEmployee(Long employeeId);
    PerformanceReviewResponse submitReview(Long reviewId);
    PerformanceReviewResponse provideFeedback(Long reviewId, String feedback, Integer rating);
    List<PerformanceReviewResponse> getAllReviews();
    void deleteReview(Long id);


}
