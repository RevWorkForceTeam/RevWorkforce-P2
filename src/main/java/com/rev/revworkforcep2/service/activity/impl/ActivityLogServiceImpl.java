package com.rev.revworkforcep2.service.activity.impl;

import com.rev.revworkforcep2.dto.response.activity.ActivityLogResponse;
import com.rev.revworkforcep2.exception.ResourceNotFoundException;
import com.rev.revworkforcep2.mapper.activity.ActivityMapper;
import com.rev.revworkforcep2.model.ActivityLog;
import com.rev.revworkforcep2.model.User;
import com.rev.revworkforcep2.repository.ActivityLogRepository;
import com.rev.revworkforcep2.repository.UserRepository;
import com.rev.revworkforcep2.service.activity.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository repository;
    private final UserRepository userRepository;
    private final ActivityMapper mapper;

    @Override
    public void log(Long userId, String action) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ActivityLog log = new ActivityLog();
        log.setAction(action);
        log.setUser(user);

        repository.save(log);
    }

    @Override
    public List<ActivityLogResponse> getAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Override
    public List<ActivityLogResponse> getByUser(Long userId) {
        return mapper.toResponseList(repository.findByUser_Id(userId));
    }
}
