package com.rev.revworkforcep2.dto.response.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagerSummaryResponse {

    private Long id;

    private String employeeId;

    private String firstName;

    private String lastName;

    private String email;

    private String departmentName;

    private String designationTitle;
}