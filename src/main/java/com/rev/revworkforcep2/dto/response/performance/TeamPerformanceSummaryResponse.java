package com.rev.revworkforcep2.dto.response.performance;

import lombok.Data;
import java.util.List;

@Data
public class TeamPerformanceSummaryResponse {

    private Long managerId;

    private Integer totalReviews;

    private Integer completedReviews;

    private Double averageRating;

    private List<PerformanceReviewResponse> reviews;
}