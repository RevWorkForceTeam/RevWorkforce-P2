package com.rev.revworkforcep2.controller.activity;

import com.rev.revworkforcep2.dto.response.activity.ActivityLogResponse;
import com.rev.revworkforcep2.service.activity.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityLogService activityService;

    @GetMapping
    public List<ActivityLogResponse> getAllActivities() {
        return activityService.getAll();
    }
}
