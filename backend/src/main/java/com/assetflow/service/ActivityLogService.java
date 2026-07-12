package com.assetflow.service;

import com.assetflow.dto.response.ActivityLogResponse;
import com.assetflow.entity.ActivityLog;
import com.assetflow.entity.Employee;
import com.assetflow.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public void log(Employee employee, String action, String details) {
        ActivityLog log = ActivityLog.builder()
                .employee(employee)
                .action(action)
                .details(details)
                .build();
        activityLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<ActivityLogResponse> getAllLogs() {
        return activityLogRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ActivityLogResponse mapToResponse(ActivityLog log) {
        return ActivityLogResponse.builder()
                .id(log.getId())
                .employeeId(log.getEmployee() != null ? log.getEmployee().getId() : null)
                .employeeName(log.getEmployee() != null ? log.getEmployee().getFullName() : "System")
                .action(log.getAction())
                .details(log.getDetails())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
