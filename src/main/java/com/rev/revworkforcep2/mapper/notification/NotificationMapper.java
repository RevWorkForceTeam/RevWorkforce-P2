package com.rev.revworkforcep2.mapper.notification;

import com.rev.revworkforcep2.dto.response.notification.NotificationResponse;
import com.rev.revworkforcep2.model.Notification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification entity) {
        return new NotificationResponse(
                entity.getId(),
                entity.getMessage(),
                entity.getType(),
                entity.isReadStatus(),
                entity.getCreatedAt()
        );
    }

    public List<NotificationResponse> toResponseList(List<Notification> list) {
        return list.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
