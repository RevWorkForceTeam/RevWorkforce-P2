package com.rev.revworkforcep2.dto.request.performance;

import lombok.Data;

@Data
public class CreateGlobalRequest {

    private Long managerId;
    private Integer year;
    private String status;
}