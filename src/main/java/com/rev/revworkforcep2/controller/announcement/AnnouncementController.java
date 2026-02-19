package com.rev.revworkforcep2.controller.announcement;

import com.rev.revworkforcep2.dto.request.announcement.CreateAnnouncementRequest;
import com.rev.revworkforcep2.dto.request.announcement.UpdateAnnouncementRequest;
import com.rev.revworkforcep2.dto.response.announcement.AnnouncementResponse;
import com.rev.revworkforcep2.service.announcement.AnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService service;

    @GetMapping
    public List<AnnouncementResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public AnnouncementResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public AnnouncementResponse create(
            @Valid @RequestBody CreateAnnouncementRequest request) {

        return service.create(request);
    }

    @PutMapping("/{id}")
    public AnnouncementResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAnnouncementRequest request) {

        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
