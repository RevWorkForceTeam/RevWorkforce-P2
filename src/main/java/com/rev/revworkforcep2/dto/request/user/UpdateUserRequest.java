package com.rev.revworkforcep2.dto.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String emergencyContact;

    private Double salary;

    private Long departmentId;
    private Long designationId;
}
