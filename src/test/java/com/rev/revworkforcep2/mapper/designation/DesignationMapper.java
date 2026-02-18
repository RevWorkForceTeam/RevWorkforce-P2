package com.rev.revworkforcep2.mapper.designation;

import com.rev.revworkforcep2.dto.request.designation.CreateDesignationRequest;
import com.rev.revworkforcep2.dto.request.designation.UpdateDesignationRequest;
import com.rev.revworkforcep2.dto.response.designation.DesignationResponse;
import com.rev.revworkforcep2.model.Designation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DesignationMapper {

    Designation toEntity(CreateDesignationRequest request);

    DesignationResponse toResponse(Designation designation);

    void updateEntityFromRequest(UpdateDesignationRequest request, @MappingTarget Designation designation);
}

