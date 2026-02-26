package com.rev.revworkforcep2.mapper.activity;

import com.rev.revworkforcep2.dto.response.activity.ActivityLogResponse;
import com.rev.revworkforcep2.model.ActivityLog;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActivityMapper {

    public ActivityLogResponse toResponse(ActivityLog entity) {
        String userName = "System";
        String userRole = "SYSTEM";
        Long userId = null;
        
        if (entity.getUser() != null) {
            userName = entity.getUser().getFirstName() + " " + entity.getUser().getLastName();
            userRole = entity.getUser().getRole().name();
            userId = entity.getUser().getId();
        }

        return new ActivityLogResponse(
                entity.getId(),
                entity.getAction(),
                userId,
                userName,
                userRole,
                entity.getCreatedAt()
        );
    }

    public List<ActivityLogResponse> toResponseList(List<ActivityLog> list) {
        return list.stream()
                .map(this::toResponse)
                .toList();
    }
}
