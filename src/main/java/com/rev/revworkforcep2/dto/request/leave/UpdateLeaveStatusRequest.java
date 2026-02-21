package com.rev.revworkforcep2.dto.request.leave;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateLeaveStatusRequest {
    private Long leaveId;
    private String status;
    private String comment;
}
