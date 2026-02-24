package com.rev.revworkforcep2.service.announcement;

import com.rev.revworkforcep2.dto.request.announcement.CreateAnnouncementRequest;
import com.rev.revworkforcep2.dto.request.announcement.UpdateAnnouncementRequest;
import com.rev.revworkforcep2.dto.response.announcement.AnnouncementResponse;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.announcement.AnnouncementMapper;
import com.rev.revworkforcep2.model.Announcement;
import com.rev.revworkforcep2.repository.AnnouncementRepository;
import com.rev.revworkforcep2.service.activity.ActivityLogService;
import com.rev.revworkforcep2.service.announcement.impl.AnnouncementServiceImpl;
import com.rev.revworkforcep2.service.notification.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnouncementServiceImplTest {

    @Mock
    private AnnouncementRepository repository;

    @Mock
    private AnnouncementMapper mapper;

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AnnouncementServiceImpl service;


    // getAll()


    @Test
    void getAll_shouldReturnMappedList() {

        List<Announcement> announcements = List.of(new Announcement());
        List<AnnouncementResponse> responses = List.of(new AnnouncementResponse());

        when(repository.findAll()).thenReturn(announcements);
        when(mapper.toResponseList(announcements)).thenReturn(responses);

        List<AnnouncementResponse> result = service.getAll();

        assertThat(result).isEqualTo(responses);
        verify(repository).findAll();
        verify(mapper).toResponseList(announcements);
    }

    // ===============================
    // getById() SUCCESS
    // ===============================

    @Test
    void getById_shouldReturnResponse_whenExists() {

        Long id = 1L;
        Announcement announcement = new Announcement();
        AnnouncementResponse response = new AnnouncementResponse();

        when(repository.findById(id)).thenReturn(Optional.of(announcement));
        when(mapper.toResponse(announcement)).thenReturn(response);

        AnnouncementResponse result = service.getById(id);

        assertThat(result).isEqualTo(response);
        verify(repository).findById(id);
        verify(mapper).toResponse(announcement);
    }

    // ===============================
    // getById() NOT FOUND
    // ===============================

    @Test
    void getById_shouldThrowException_whenNotFound() {

        when(repository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class,
                        () -> service.getById(1L));

        assertThat(exception.getMessage()).isEqualTo("Announcement not found");
        assertThat(exception.getStatus()).isEqualTo(404);
    }


    // create()


    @Test
    void create_shouldSaveLogAndNotify() {

        CreateAnnouncementRequest request = new CreateAnnouncementRequest();
        request.setTitle("New Policy");
        request.setContent("Policy content");

        Announcement saved = new Announcement();
        saved.setTitle("New Policy");

        AnnouncementResponse response = new AnnouncementResponse();

        when(repository.save(any())).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        AnnouncementResponse result = service.create(request);

        assertThat(result).isEqualTo(response);

        verify(repository).save(any(Announcement.class));
        verify(activityLogService).log(1L, "Created Announcement: New Policy");
        verify(notificationService)
                .triggerForAllUsers("New Announcement: New Policy", "ANNOUNCEMENT");
        verify(mapper).toResponse(saved);
    }

    // update() SUCCESS

    @Test
    void update_shouldModifyAndLog() {

        Long id = 1L;

        UpdateAnnouncementRequest request = new UpdateAnnouncementRequest();
        request.setTitle("Updated Title");
        request.setContent("Updated Content");

        Announcement existing = new Announcement();
        existing.setTitle("Old Title");

        Announcement updated = new Announcement();
        updated.setTitle("Updated Title");

        AnnouncementResponse response = new AnnouncementResponse();

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(updated);
        when(mapper.toResponse(updated)).thenReturn(response);

        AnnouncementResponse result = service.update(id, request);

        assertThat(result).isEqualTo(response);

        verify(activityLogService)
                .log(1L, "Updated Announcement: Updated Title");
        verify(repository).save(existing);
    }


    // update() NOT FOUND


    @Test
    void update_shouldThrowException_whenNotFound() {

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(1L, new UpdateAnnouncementRequest()));
    }


    // delete() SUCCESS

    @Test
    void delete_shouldRemoveAndLog() {

        Long id = 1L;

        Announcement announcement = new Announcement();
        announcement.setTitle("Test Title");

        when(repository.findById(id)).thenReturn(Optional.of(announcement));

        service.delete(id);

        verify(repository).delete(announcement);
        verify(activityLogService)
                .log(1L, "Deleted Announcement: Test Title");
    }


    // delete() NOT FOUND


    @Test
    void delete_shouldThrowException_whenNotFound() {

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.delete(1L));
    }
}