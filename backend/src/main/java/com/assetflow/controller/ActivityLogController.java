package com.assetflow.controller;

import com.assetflow.dto.response.ActivityLogResponse;
import com.assetflow.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/activity-logs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSET_MANAGER')")
    public List<ActivityLogResponse> getAllLogs() {
        return activityLogService.getAllLogs();
    }
}
