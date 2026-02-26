package com.rev.revworkforcep2.dto.request.performance;

import lombok.Data;

@Data
public class AddGoalCommentRequest {
    private Long goalId;
    private String comment;
}
