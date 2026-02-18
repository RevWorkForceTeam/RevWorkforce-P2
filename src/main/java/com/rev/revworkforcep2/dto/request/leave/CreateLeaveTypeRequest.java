package com.rev.revworkforcep2.dto.request.leave;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLeaveTypeRequest {
    private String name;
    private int defaultQuota;
}
