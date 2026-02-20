package com.rev.revworkforcep2.service.activity;

import com.rev.revworkforcep2.dto.response.activity.ActivityLogResponse;

import java.util.List;

public interface ActivityLogService {

    void log(Long userId, String action);

    List<ActivityLogResponse> getAll();

    List<ActivityLogResponse> getByUser(Long userId);
}
