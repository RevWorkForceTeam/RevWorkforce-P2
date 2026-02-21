package com.rev.revworkforcep2.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignManagerRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long managerId;
}