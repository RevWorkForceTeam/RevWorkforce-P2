package com.rev.revworkforcep2.dto.request.leave;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdjustLeaveBalanceRequest {
    private Long employeeId;
    private Long leaveTypeId;
    private Integer days;
    private String reason;
}
