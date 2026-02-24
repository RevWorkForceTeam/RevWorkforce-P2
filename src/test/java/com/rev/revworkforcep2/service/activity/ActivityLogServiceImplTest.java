package com.rev.revworkforcep2.service.activity;

import com.rev.revworkforcep2.dto.response.activity.ActivityLogResponse;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.activity.ActivityMapper;
import com.rev.revworkforcep2.model.ActivityLog;
import com.rev.revworkforcep2.model.User;
import com.rev.revworkforcep2.repository.ActivityLogRepository;
import com.rev.revworkforcep2.repository.UserRepository;
import com.rev.revworkforcep2.service.activity.impl.ActivityLogServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityLogServiceImplTest {

    @Mock
    private ActivityLogRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivityMapper mapper;

    @InjectMocks
    private ActivityLogServiceImpl service;


    // log() SUCCESS


    @Test
    void log_shouldSaveActivity_whenUserExists() {

        Long userId = 1L;
        String action = "LOGIN";

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        service.log(userId, action);

        ArgumentCaptor<ActivityLog> captor = ArgumentCaptor.forClass(ActivityLog.class);
        verify(repository).save(captor.capture());

        ActivityLog savedLog = captor.getValue();

        assertThat(savedLog.getAction()).isEqualTo(action);
        assertThat(savedLog.getUser()).isEqualTo(user);
    }


    // log() USER NOT FOUND


    @Test
    void log_shouldThrowResourceNotFoundException_whenUserDoesNotExist() {

        Long userId = 99L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(ResourceNotFoundException.class,
                        () -> service.log(userId, "LOGIN"));

        assertThat(exception.getMessage()).isEqualTo("User not found");
        assertThat(exception.getStatus()).isEqualTo(404);

        verify(repository, never()).save(any());
    }


    // getAll()


    @Test
    void getAll_shouldReturnMappedResponseList() {

        List<ActivityLog> logs = List.of(new ActivityLog(), new ActivityLog());
        List<ActivityLogResponse> responses = List.of(
                new ActivityLogResponse(),
                new ActivityLogResponse()
        );

        when(repository.findAll()).thenReturn(logs);
        when(mapper.toResponseList(logs)).thenReturn(responses);

        List<ActivityLogResponse> result = service.getAll();

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(responses);

        verify(repository).findAll();
        verify(mapper).toResponseList(logs);
    }


    // getByUser()


    @Test
    void getByUser_shouldReturnMappedResponseList() {

        Long userId = 1L;

        List<ActivityLog> logs = List.of(new ActivityLog());
        List<ActivityLogResponse> responses = List.of(new ActivityLogResponse());

        when(repository.findByUser_Id(userId)).thenReturn(logs);
        when(mapper.toResponseList(logs)).thenReturn(responses);

        List<ActivityLogResponse> result = service.getByUser(userId);

        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(responses);

        verify(repository).findByUser_Id(userId);
        verify(mapper).toResponseList(logs);
    }
}