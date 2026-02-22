package com.rev.revworkforcep2.mapper.leave;

import com.rev.revworkforcep2.dto.request.leave.*;
import com.rev.revworkforcep2.dto.response.leave.*;
import com.rev.revworkforcep2.model.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface LeaveMapper {

    // =====================================================
    // LeaveApplication Mapping
    // =====================================================

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "startDate", source = "request.fromDate")
    @Mapping(target = "endDate", source = "request.toDate")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "leaveType", source = "leaveType")
    LeaveApplication toEntity(ApplyLeaveRequest request, User user, LeaveType leaveType);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "leaveTypeId", source = "leaveType.id")
    @Mapping(target = "fromDate", source = "startDate")
    @Mapping(target = "toDate", source = "endDate")
    LeaveApplicationResponse toResponse(LeaveApplication entity);


    // =====================================================
    // LeaveType Mapping
    // =====================================================

    LeaveType toEntity(CreateLeaveTypeRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateLeaveTypeRequest request, @MappingTarget LeaveType entity);

    LeaveTypeResponse toResponse(LeaveType entity);


    // =====================================================
    // Holiday Mapping
    // =====================================================

    Holiday toEntity(CreateHolidayRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UpdateHolidayRequest request, @MappingTarget Holiday entity);

    HolidayResponse toResponse(Holiday entity);


    // =====================================================
    // LeaveBalance Mapping
    // =====================================================

    @Mapping(target = "employeeId", source = "user.id")
    @Mapping(target = "leaveTypeId", source = "leaveType.id")
    @Mapping(target = "totalQuota", source = "totalDays")
    @Mapping(target = "used", source = "usedDays")
    @Mapping(target = "remaining", source = "remainingDays")
    LeaveBalanceResponse toResponse(LeaveBalance entity);



    // ===============================
// Team Calendar Mapping
// ===============================

    default TeamLeaveCalenderResponse toTeamCalendar(LeaveApplication leave) {

        if (leave == null) return null;

        TeamLeaveCalenderResponse dto = new TeamLeaveCalenderResponse();

        dto.setEmployeeId(leave.getUser().getId());
        dto.setEmployeeName(leave.getUser().getFirstName());
        dto.setEmployeeName(leave.getUser().getLastName());
        dto.setStartDate(leave.getStartDate());
        dto.setEndDate(leave.getEndDate());
        dto.setLeaveType(leave.getLeaveType().getName());
        dto.setStatus(leave.getStatus().name());

        return dto;
    }

    default TeamLeaveCalenderResponse holidayToTeamCalendar(Holiday holiday) {

        if (holiday == null) return null;

        TeamLeaveCalenderResponse dto = new TeamLeaveCalenderResponse();

        dto.setEmployeeId(null);
        dto.setEmployeeName("Holiday");
        dto.setStartDate(holiday.getHolidayDate());
        dto.setEndDate(holiday.getHolidayDate());
        dto.setLeaveType(holiday.getName());
        dto.setStatus("HOLIDAY");

        return dto;
    }

}
