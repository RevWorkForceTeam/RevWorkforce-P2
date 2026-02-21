package com.rev.revworkforcep2.dto.request.performance;

import lombok.Data;

@Data
public class UpdateGoalProgressRequest {

    private Long goalId;      // Required

    private Integer progress; // Must be between 0 and 100
}