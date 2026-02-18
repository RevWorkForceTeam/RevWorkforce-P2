package com.rev.revworkforcep2.mapper.department;

import com.rev.revworkforcep2.dto.request.department.CreateDepartmentRequest;
import com.rev.revworkforcep2.dto.request.department.UpdateDepartmentRequest;
import com.rev.revworkforcep2.dto.response.department.DepartmentResponse;
import com.rev.revworkforcep2.model.Department;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    Department toEntity(CreateDepartmentRequest request);

    DepartmentResponse toResponse(Department department);

    void updateEntityFromRequest(UpdateDepartmentRequest request, @MappingTarget Department department);
}

