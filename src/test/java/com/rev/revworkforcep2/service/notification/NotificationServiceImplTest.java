package com.rev.revworkforcep2.service.notification;

import com.rev.revworkforcep2.dto.response.notification.NotificationResponse;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.notification.NotificationMapper;
import com.rev.revworkforcep2.model.Notification;
import com.rev.revworkforcep2.model.User;
import com.rev.revworkforcep2.repository.NotificationRepository;
import com.rev.revworkforcep2.repository.UserRepository;
import com.rev.revworkforcep2.security.model.CustomUserDetails;
import com.rev.revworkforcep2.service.notification.impl.NotificationServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository repository;

    @Mock
    private NotificationMapper mapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl service;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // GET ALL
    @Test
    void getAll_shouldReturnMappedList() {

        List<Notification> notifications = List.of(new Notification());
        List<NotificationResponse> responses = List.of(new NotificationResponse());

        when(repository.findAll()).thenReturn(notifications);
        when(mapper.toResponseList(notifications)).thenReturn(responses);

        List<NotificationResponse> result = service.getAll();

        assertThat(result).isEqualTo(responses);
        verify(repository).findAll();
    }

    // GET MY NOTIFICATIONS
    @Test
    void getMyNotifications_shouldReturnUserNotifications() {

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(1L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null)
        );

        List<Notification> notifications = List.of(new Notification());
        List<NotificationResponse> responses = List.of(new NotificationResponse());

        when(repository.findByUser_Id(1L)).thenReturn(notifications);
        when(mapper.toResponseList(notifications)).thenReturn(responses);

        List<NotificationResponse> result = service.getMyNotifications();

        assertThat(result).isEqualTo(responses);
        verify(repository).findByUser_Id(1L);
    }

    // MARK AS READ SUCCESS
    @Test
    void markAsRead_shouldUpdateStatus() {

        Notification notification = new Notification();
        notification.setReadStatus(false);

        when(repository.findById(1L)).thenReturn(Optional.of(notification));

        service.markAsRead(1L);

        assertThat(notification.isReadStatus()).isTrue();
        verify(repository).save(notification);
    }

    // MARK AS READ NOT FOUND
    @Test
    void markAsRead_shouldThrowException_whenNotFound() {

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.markAsRead(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Notification not found");
    }

    // TRIGGER FOR ALL USERS
    @Test
    void triggerForAllUsers_shouldCreateNotificationsForEachUser() {

        User user1 = new User();
        User user2 = new User();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        service.triggerForAllUsers("Message", "TYPE");

        verify(repository, times(2)).save(any(Notification.class));
    }

    // TRIGGER FOR USER SUCCESS
    @Test
    void triggerForUser_shouldCreateNotification() {

        User user = new User();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        service.triggerForUser(1L, "Message", "TYPE");

        verify(repository).save(any(Notification.class));
    }

    // TRIGGER FOR USER NOT FOUND
    @Test
    void triggerForUser_shouldThrowException_whenUserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.triggerForUser(1L, "Message", "TYPE"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    // GET UNREAD COUNT
    @Test
    void getUnreadCount_shouldReturnCount() {

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(1L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null)
        );

        when(repository.countByUser_IdAndReadStatusFalse(1L)).thenReturn(5L);

        long count = service.getUnreadCount();

        assertThat(count).isEqualTo(5L);
        verify(repository).countByUser_IdAndReadStatusFalse(1L);
    }
}