package com.rev.revworkforcep2.mapper.user;


import com.rev.revworkforcep2.dto.request.user.CreateUserRequest;
import com.rev.revworkforcep2.dto.request.user.UpdateUserRequest;
import com.rev.revworkforcep2.dto.response.user.UserResponse;
import com.rev.revworkforcep2.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // -------------------------
    // Create Mapping
    // -------------------------
    User toEntity(CreateUserRequest request);


    // -------------------------
    // Entity → Response Mapping
    // -------------------------
    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.name", target = "departmentName")
    @Mapping(source = "designation.id", target = "designationId")
    @Mapping(source = "designation.title", target = "designationTitle")
    @Mapping(source = "manager.id", target = "managerId")
    UserResponse toResponse(User user);


    // -------------------------
    // Update Mapping
    // -------------------------
    void updateEntityFromRequest(UpdateUserRequest request,
                                 @MappingTarget User user);
}



