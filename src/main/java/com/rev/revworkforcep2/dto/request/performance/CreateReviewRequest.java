package com.rev.revworkforcep2.dto.request.performance;
import lombok.Data;

@Data
public class CreateReviewRequest {
    private Long employeeId;
    private String deliverables;
    private String accomplishments;
    private String improvementAreas;
    private Integer selfRating;
}
