package com.rev.revworkforcep2.service.announcement.impl;

import com.rev.revworkforcep2.dto.request.announcement.CreateAnnouncementRequest;
import com.rev.revworkforcep2.dto.request.announcement.UpdateAnnouncementRequest;
import com.rev.revworkforcep2.dto.response.announcement.AnnouncementResponse;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.announcement.AnnouncementMapper;
import com.rev.revworkforcep2.model.Announcement;
import com.rev.revworkforcep2.repository.AnnouncementRepository;
import com.rev.revworkforcep2.service.activity.ActivityLogService;
import com.rev.revworkforcep2.service.announcement.AnnouncementService;
import com.rev.revworkforcep2.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository repository;
    private final AnnouncementMapper mapper;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;

    @Override
    public List<AnnouncementResponse> getAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public AnnouncementResponse getById(Long id) {

        Announcement announcement = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Announcement not found"));

        return mapper.toResponse(announcement);
    }
    @Override
    public AnnouncementResponse create(CreateAnnouncementRequest request) {

        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());

        Announcement saved = repository.save(announcement);

        activityLogService.log(1L,
                "Created Announcement: " + saved.getTitle());

        notificationService.triggerForAllUsers(
                "New Announcement: " + saved.getTitle(),
                "ANNOUNCEMENT"
        );

        return mapper.toResponse(saved);
    }


    @Override
    public AnnouncementResponse update(Long id, UpdateAnnouncementRequest request) {

        Announcement announcement = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Announcement not found"));

        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());

        Announcement updated = repository.save(announcement);

        activityLogService.log(1L,
                "Updated Announcement: " + updated.getTitle());

        return mapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {

        Announcement announcement = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Announcement not found"));

        repository.delete(announcement);

        activityLogService.log(1L,
                "Deleted Announcement: " + announcement.getTitle());
    }
}
