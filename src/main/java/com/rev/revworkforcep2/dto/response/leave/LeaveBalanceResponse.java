package com.rev.revworkforcep2.dto.response.leave;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveBalanceResponse {
    private Long employeeId;
    private String userName;
    private Long leaveTypeId;
    private String leaveTypeName;
    private Integer totalQuota;
    private Integer used;
    private Integer remaining;
}
