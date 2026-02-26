package com.rev.revworkforcep2.service.leave.impl;

import com.rev.revworkforcep2.dto.request.leave.CreateHolidayRequest;
import com.rev.revworkforcep2.dto.request.leave.UpdateHolidayRequest;
import com.rev.revworkforcep2.dto.response.leave.HolidayResponse;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.leave.LeaveMapper;
import com.rev.revworkforcep2.model.Holiday;
import com.rev.revworkforcep2.repository.HolidayRepository;
import com.rev.revworkforcep2.service.leave.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository holidayRepository;
    private final LeaveMapper leaveMapper;

    @Override
    public HolidayResponse createHoliday(CreateHolidayRequest request) {

        Holiday entity = leaveMapper.toEntity(request);

        Holiday saved = holidayRepository.save(entity);

        return leaveMapper.toResponse(saved);
    }

    @Override
    public HolidayResponse updateHoliday(Long id, UpdateHolidayRequest request) {

        Holiday entity = holidayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found"));

        leaveMapper.updateEntity(request, entity);

        Holiday updated = holidayRepository.save(entity);

        return leaveMapper.toResponse(updated);
    }

    @Override
    public HolidayResponse getHolidayById(Long id) {

        Holiday entity = holidayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found"));

        return leaveMapper.toResponse(entity);
    }

    @Override
    public List<HolidayResponse> getAllHolidays() {

        return holidayRepository.findAll()
                .stream()
                .map(leaveMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteHoliday(Long id) {

        Holiday entity = holidayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday not found"));

        holidayRepository.delete(entity);
    }

}
