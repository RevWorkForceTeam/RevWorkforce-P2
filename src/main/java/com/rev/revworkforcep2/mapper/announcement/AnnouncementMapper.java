package com.rev.revworkforcep2.mapper.announcement;

import com.rev.revworkforcep2.dto.response.announcement.AnnouncementResponse;
import com.rev.revworkforcep2.model.Announcement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AnnouncementMapper {

    public AnnouncementResponse toResponse(Announcement entity) {
        return new AnnouncementResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }

    public List<AnnouncementResponse> toResponseList(List<Announcement> list) {
        return list.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
