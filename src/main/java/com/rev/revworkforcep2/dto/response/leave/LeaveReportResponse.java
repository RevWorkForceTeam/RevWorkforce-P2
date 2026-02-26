package com.rev.revworkforcep2.dto.response.leave;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveReportResponse {
    private String name;
    private String department;
    private int totalLeaves;
    private int approvedLeaves;
    private int pendingLeaves;
    private int rejectedLeaves;
    private int casualLeaves;
    private int sickLeaves;
    private int paidLeaves;
}
