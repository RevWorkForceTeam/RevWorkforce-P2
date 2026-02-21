package com.rev.revworkforcep2.dto.request.performance;

import lombok.Data;

@Data
public class ProvidedFeedbackRequest {

    private String feedback;   // Required

    private Integer rating;    // Must be between 1 and 5
}