package com.rev.revworkforcep2.mapper.activity;

import com.rev.revworkforcep2.dto.response.activity.ActivityLogResponse;
import com.rev.revworkforcep2.model.ActivityLog;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ActivityMapper {

    public ActivityLogResponse toResponse(ActivityLog entity) {
        return new ActivityLogResponse(
                entity.getId(),
                entity.getAction(),
                entity.getUser().getId(),
                entity.getCreatedAt()
        );
    }

    public List<ActivityLogResponse> toResponseList(List<ActivityLog> list) {
        return list.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}