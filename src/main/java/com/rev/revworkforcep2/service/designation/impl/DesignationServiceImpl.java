package com.rev.revworkforcep2.service.designation.impl;

import com.rev.revworkforcep2.dto.request.designation.CreateDesignationRequest;
import com.rev.revworkforcep2.dto.request.designation.UpdateDesignationRequest;
import com.rev.revworkforcep2.dto.response.designation.DesignationResponse;
import com.rev.revworkforcep2.exception.BusinessValidationException;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.designation.DesignationMapper;
import com.rev.revworkforcep2.model.Designation;
import com.rev.revworkforcep2.repository.DesignationRepository;
import com.rev.revworkforcep2.service.designation.DesignationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DesignationServiceImpl implements DesignationService {

    private final DesignationRepository designationRepository;
    private final DesignationMapper designationMapper;

    @Override
    public DesignationResponse createDesignation(CreateDesignationRequest request) {

        if (designationRepository.existsByTitle(request.getTitle())) {
            throw new BusinessValidationException("Designation with this title already exists");
        }

        Designation designation = designationMapper.toEntity(request);
        Designation savedDesignation = designationRepository.save(designation);

        return designationMapper.toResponse(savedDesignation);
    }

    @Override
    public DesignationResponse updateDesignation(Long id, UpdateDesignationRequest request) {

        Designation designation = designationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found with id: " + id));

        if (!designation.getTitle().equals(request.getTitle())
                && designationRepository.existsByTitle(request.getTitle())) {
            throw new BusinessValidationException("Designation with this title already exists");
        }

        designationMapper.updateEntityFromRequest(request, designation);

        Designation updatedDesignation = designationRepository.save(designation);

        return designationMapper.toResponse(updatedDesignation);
    }

    @Override
    public DesignationResponse getDesignationById(Long id) {

        Designation designation = designationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found with id: " + id));

        return designationMapper.toResponse(designation);
    }

    @Override
    public List<DesignationResponse> getAllDesignations() {

        List<Designation> designations = designationRepository.findAll();

        return designations.stream()
                .map(designationMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteDesignation(Long id) {

        Designation designation = designationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found with id: " + id));

        if (designation.getUsers() != null && !designation.getUsers().isEmpty()) {
            throw new BusinessValidationException("Cannot delete designation assigned to users");
        }

        designationRepository.delete(designation);
    }
}

