package com.rev.revworkforcep2.service.performance;

import com.rev.revworkforcep2.dto.request.performance.*;
import com.rev.revworkforcep2.dto.response.performance.PerformanceReviewResponse;
import com.rev.revworkforcep2.dto.response.performance.TeamPerformanceSummaryResponse;

import java.util.List;

public interface PerformanceReviewService {

    PerformanceReviewResponse createReview(CreateReviewRequest request);

    PerformanceReviewResponse getReviewById(Long id);
    TeamPerformanceSummaryResponse getTeamPerformanceSummary(
            CreateGlobalRequest request);
    PerformanceReviewResponse createPerformanceReview(
            CreatePerformanceReviewRequest request);
    List<PerformanceReviewResponse> getReviewsByEmployee(Long employeeId);
    PerformanceReviewResponse submitReview(Long reviewId);
    PerformanceReviewResponse provideFeedback(Long reviewId, String feedback, Integer rating);
    List<PerformanceReviewResponse> getAllReviews();
    PerformanceReviewResponse provideFeedback(
            Long reviewId,
            ProvidedFeedbackRequest request);
    PerformanceReviewResponse submitReview(
            SubmitPerformanceReviewRequest request);
    void deleteReview(Long id);


}
