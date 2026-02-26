package com.rev.revworkforcep2.dto.request.performance;
import lombok.Data;

@Data
public class CreatePerformanceReviewRequest {

    private Long employeeId;

    private Integer year;

    private String deliverables;

    private String accomplishments;

    private String improvements;

    private Integer selfRating;
}

