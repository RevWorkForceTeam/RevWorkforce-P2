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
}
