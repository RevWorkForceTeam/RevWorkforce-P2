package com.rev.revworkforcep2.dto.response.leave;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveTypeResponse {
    private Long id;
    private String name;
    private Integer defaultQuota;
}
