package com.rev.revworkforcep2.dto.response.performance;
import lombok.Data;
import java.time.LocalDate;

@Data
public class GoalResponse {
    private Long id;
    private String description;
    private LocalDate deadline;
    private String priority;
    private String status;
    private Integer progress;
}
