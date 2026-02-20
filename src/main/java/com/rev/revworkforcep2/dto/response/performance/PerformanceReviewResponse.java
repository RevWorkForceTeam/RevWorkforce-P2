package com.rev.revworkforcep2.dto.response.performance;
import lombok.Data;

@Data
public class PerformanceReviewResponse {
    private Long id;
    private String deliverables;
    private String accomplishments;
    private String improvementAreas;
    private Integer selfRating;
    private String status;
    private Integer managerRating;
    private String managerFeedback;
}
