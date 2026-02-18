package com.rev.revworkforcep2.dto.request.leave;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateLeaveTypeRequest {
    private String name;
    private Integer defaultQuota;
}
