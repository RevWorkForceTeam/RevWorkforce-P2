package com.rev.revworkforcep2.dto.request.performance;
import lombok.Data;

@Data
public class CreatePerformanceReviewRequest {

    private Long employeeId;      // Required

    private Integer year;         // Required

    private String deliverables;

    private String accomplishments;

    private String improvements;

    private Integer selfRating;   // Must be between 1 and 5
}

