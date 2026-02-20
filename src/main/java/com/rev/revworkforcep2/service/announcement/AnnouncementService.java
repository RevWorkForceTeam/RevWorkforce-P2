package com.rev.revworkforcep2.service.announcement;

import com.rev.revworkforcep2.dto.request.announcement.CreateAnnouncementRequest;
import com.rev.revworkforcep2.dto.request.announcement.UpdateAnnouncementRequest;
import com.rev.revworkforcep2.dto.response.announcement.AnnouncementResponse;

import java.util.List;

public interface AnnouncementService {

    List<AnnouncementResponse> getAll();

    AnnouncementResponse getById(Long id);

    AnnouncementResponse create(CreateAnnouncementRequest request);

    AnnouncementResponse update(Long id, UpdateAnnouncementRequest request);

    void delete(Long id);
}
