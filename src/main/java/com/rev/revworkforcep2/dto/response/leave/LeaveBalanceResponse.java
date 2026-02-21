package com.rev.revworkforcep2.dto.response.leave;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveBalanceResponse {
    private Long employeeId;
    private Long leaveTypeId;
    private Integer totalQuota;
    private Integer used;
    private Integer remaining;
}
