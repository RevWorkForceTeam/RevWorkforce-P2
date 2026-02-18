package com.rev.revworkforcep2.service.designation;


import com.rev.revworkforcep2.dto.request.designation.CreateDesignationRequest;
import com.rev.revworkforcep2.dto.request.designation.UpdateDesignationRequest;
import com.rev.revworkforcep2.dto.response.designation.DesignationResponse;

import java.util.List;

public interface DesignationService {

    DesignationResponse createDesignation(CreateDesignationRequest request);

    DesignationResponse updateDesignation(Long id, UpdateDesignationRequest request);

    DesignationResponse getDesignationById(Long id);

    List<DesignationResponse> getAllDesignations();

    void deleteDesignation(Long id);
}

