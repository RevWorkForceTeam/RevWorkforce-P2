package com.rev.revworkforcep2.service.notification.impl;

import com.rev.revworkforcep2.dto.response.notification.NotificationResponse;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.notification.NotificationMapper;
import com.rev.revworkforcep2.model.Notification;
import com.rev.revworkforcep2.model.User;
import com.rev.revworkforcep2.repository.NotificationRepository;
import com.rev.revworkforcep2.repository.UserRepository;
import com.rev.revworkforcep2.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final NotificationMapper mapper;
    private final UserRepository userRepository;

    @Override
    public List<NotificationResponse> getAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public List<NotificationResponse> getByUser(Long userId) {
        return mapper.toResponseList(repository.findByUser_Id(userId));
    }

    @Override
    public void markAsRead(Long id) {

        Notification notification = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Notification not found"));

        notification.setReadStatus(true);
        repository.save(notification);
    }

    @Override
    public void triggerForAllUsers(String message, String type) {

        List<User> users = userRepository.findAll();

        for (User user : users) {
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setMessage(message);
            notification.setType(type);
            notification.setReadStatus(false);
            repository.save(notification);
        }
    }

    @Override
    public void triggerForUser(Long userId, String message, String type) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setReadStatus(false);

        repository.save(notification);
    }
}

