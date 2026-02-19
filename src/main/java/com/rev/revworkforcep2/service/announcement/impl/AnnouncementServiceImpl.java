package com.rev.revworkforcep2.service.announcement.impl;

import com.rev.revworkforcep2.dto.request.announcement.CreateAnnouncementRequest;
import com.rev.revworkforcep2.dto.request.announcement.UpdateAnnouncementRequest;
import com.rev.revworkforcep2.dto.response.announcement.AnnouncementResponse;
import com.rev.revworkforcep2.mapper.announcement.AnnouncementMapper;
import com.rev.revworkforcep2.model.Announcement;
import com.rev.revworkforcep2.repository.AnnouncementRepository;
import com.rev.revworkforcep2.service.announcement.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository repository;
    private final AnnouncementMapper mapper;

    @Override
    public List<AnnouncementResponse> getAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public AnnouncementResponse getById(Long id) {
        Announcement announcement = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        return mapper.toResponse(announcement);
    }

    @Override
    public AnnouncementResponse create(CreateAnnouncementRequest request) {

        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());

        return mapper.toResponse(repository.save(announcement));
    }

    @Override
    public AnnouncementResponse update(Long id, UpdateAnnouncementRequest request) {

        Announcement announcement = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));

        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());

        return mapper.toResponse(repository.save(announcement));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
