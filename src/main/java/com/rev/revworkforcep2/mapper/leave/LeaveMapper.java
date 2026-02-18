package com.rev.revworkforcep2.mapper.leave;

import com.rev.revworkforcep2.dto.request.leave.*;
import com.rev.revworkforcep2.dto.response.leave.*;
import com.rev.revworkforcep2.model.*;
import org.springframework.stereotype.Component;

@Component
public class LeaveMapper {

    // =====================================================
    // LeaveApplication Mapping
    // =====================================================
    public LeaveApplication toLeaveApplication(ApplyLeaveRequest request, User user, LeaveType leaveType) {
        LeaveApplication entity = new LeaveApplication();
        entity.setUser(user);
        entity.setLeaveType(leaveType);
        entity.setStartDate(request.getFromDate());
        entity.setEndDate(request.getToDate());
        entity.setReason(request.getReason());
        return entity;
    }

    public LeaveApplicationResponse toLeaveApplicationResponse(LeaveApplication entity) {
        LeaveApplicationResponse response = new LeaveApplicationResponse();
        response.setId(entity.getId());
        response.setUserId(entity.getUser().getId());
        response.setLeaveTypeId(entity.getLeaveType().getId());
        response.setFromDate(entity.getStartDate());
        response.setToDate(entity.getEndDate());
        response.setReason(entity.getReason());
        response.setStatus(entity.getStatus());
        return response;
    }

    public LeaveApplication toEntity(ApplyLeaveRequest request, User user, LeaveType leaveType) {
        return toLeaveApplication(request, user, leaveType);
    }

    public LeaveApplicationResponse toResponse(LeaveApplication entity) {
        return toLeaveApplicationResponse(entity);
    }

    // =====================================================
    // LeaveType Mapping
    // =====================================================
    public LeaveType toLeaveType(CreateLeaveTypeRequest request) {
        LeaveType entity = new LeaveType();
        entity.setName(request.getName());
        entity.setDefaultQuota(request.getDefaultQuota());
        return entity;
    }

    public void updateLeaveType(LeaveType entity, UpdateLeaveTypeRequest request) {
        entity.setName(request.getName());
        entity.setDefaultQuota(request.getDefaultQuota());
    }

    public LeaveTypeResponse toLeaveTypeResponse(LeaveType entity) {
        LeaveTypeResponse response = new LeaveTypeResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDefaultQuota(entity.getDefaultQuota());
        return response;
    }

    public void updateEntity(LeaveType entity, UpdateLeaveTypeRequest request) {
        updateLeaveType(entity, request);
    }

    // =====================================================
    // Holiday Mapping
    // =====================================================
    public Holiday toHolidayEntity(CreateHolidayRequest request) {
        Holiday entity = new Holiday();
        entity.setName(request.getName());
        entity.setHolidayDate(request.getHolidayDate());
        return entity;
    }

    public void updateHolidayEntity(Holiday entity, UpdateHolidayRequest request) {
        if (request.getName() != null) entity.setName(request.getName());
        if (request.getHolidayDate() != null) entity.setHolidayDate(request.getHolidayDate());
    }

    public HolidayResponse toHolidayResponse(Holiday entity) {
        HolidayResponse response = new HolidayResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setHolidayDate(entity.getHolidayDate());
        return response;
    }


    // =====================================================
    // LeaveBalance Mapping
    // =====================================================
    public LeaveBalanceResponse toLeaveBalanceResponse(LeaveBalance entity) {
        LeaveBalanceResponse res = new LeaveBalanceResponse();
        res.setEmployeeId(entity.getUser().getId());
        res.setLeaveTypeId(entity.getLeaveType().getId());
        res.setTotalQuota(entity.getTotalDays());
        res.setUsed(entity.getUsedDays());
        res.setRemaining(entity.getRemainingDays());
        return res;
    }
}
