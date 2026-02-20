package com.rev.revworkforcep2.dto.request.performance;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateGoalRequest {
    private Long employeeId;
    private String description;
    private LocalDate deadline;
    private String priority;
}
