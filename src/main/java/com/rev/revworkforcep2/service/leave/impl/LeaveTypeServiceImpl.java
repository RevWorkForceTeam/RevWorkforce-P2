package com.rev.revworkforcep2.service.leave.impl;

import com.rev.revworkforcep2.dto.request.leave.CreateLeaveTypeRequest;
import com.rev.revworkforcep2.dto.request.leave.UpdateLeaveTypeRequest;
import com.rev.revworkforcep2.dto.response.leave.LeaveTypeResponse;
import com.rev.revworkforcep2.exception.ConflictException;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.leave.LeaveMapper;
import com.rev.revworkforcep2.model.LeaveType;
import com.rev.revworkforcep2.repository.LeaveTypeRepository;
import com.rev.revworkforcep2.service.leave.LeaveTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveMapper leaveMapper;

    @Override
    public LeaveTypeResponse createLeaveType(CreateLeaveTypeRequest request) {

        if (leaveTypeRepository.existsByName(request.getName())) {
            throw new ConflictException(
                    "LeaveType already exists with name: " + request.getName()
            );
        }

        LeaveType entity = leaveMapper.toEntity(request);
        LeaveType saved = leaveTypeRepository.save(entity);

        return leaveMapper.toResponse(saved);
    }

    @Override
    public LeaveTypeResponse updateLeaveType(Long id, UpdateLeaveTypeRequest request) {

        LeaveType entity = leaveTypeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("LeaveType not found with id: " + id)
                );

        if (!entity.getName().equalsIgnoreCase(request.getName())
                && leaveTypeRepository.existsByName(request.getName())) {

            throw new ConflictException(
                    "LeaveType already exists with name: " + request.getName()
            );
        }

        leaveMapper.updateEntity(request, entity);

        LeaveType updated = leaveTypeRepository.save(entity);

        return leaveMapper.toResponse(updated);
    }

    @Override
    public LeaveTypeResponse getLeaveTypeById(Long id) {

        LeaveType entity = leaveTypeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("LeaveType not found with id: " + id)
                );

        return leaveMapper.toResponse(entity);
    }

    @Override
    public List<LeaveTypeResponse> getAllLeaveTypes() {

        return leaveTypeRepository.findAll()
                .stream()
                .map(leaveMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteLeaveType(Long id) {

        LeaveType entity = leaveTypeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("LeaveType not found with id: " + id)
                );

        leaveTypeRepository.delete(entity);
    }
//    @Override
//    public List<LeaveType> getAllLeaveTypeEntities() {
//        return leaveTypeRepository.findAll();
//    }
}
