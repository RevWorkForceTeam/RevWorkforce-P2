package com.rev.revworkforcep2.mapper.user;

import com.rev.revworkforcep2.dto.request.user.CreateUserRequest;
import com.rev.revworkforcep2.dto.request.user.UpdateUserRequest;
import com.rev.revworkforcep2.dto.response.user.ManagerSummaryResponse;
import com.rev.revworkforcep2.dto.response.user.UserResponse;
import com.rev.revworkforcep2.dto.response.user.UserSummaryResponse;
import com.rev.revworkforcep2.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(CreateUserRequest request);

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.name", target = "departmentName")
    @Mapping(source = "designation.id", target = "designationId")
    @Mapping(source = "designation.title", target = "designationTitle")
    @Mapping(source = "manager", target = "manager")
    UserResponse toResponse(User user);

    @Mapping(source = "department.name", target = "departmentName")
    @Mapping(source = "designation.title", target = "designationTitle")
    UserSummaryResponse toSummaryResponse(User user);

    @Mapping(source = "department.name", target = "departmentName")
    @Mapping(source = "designation.title", target = "designationTitle")
    ManagerSummaryResponse toManagerSummary(User user);

    void updateEntityFromRequest(UpdateUserRequest request,
                                 @MappingTarget User user);
}