package com.assetflow.service;

import com.assetflow.dto.request.MaintenanceRequestDto;
import com.assetflow.dto.request.MaintenanceUpdateDto;
import com.assetflow.dto.response.MaintenanceResponse;
import com.assetflow.entity.*;
import com.assetflow.exception.ApiException;
import com.assetflow.exception.ResourceNotFoundException;
import com.assetflow.repository.AssetRepository;
import com.assetflow.repository.EmployeeRepository;
import com.assetflow.repository.MaintenanceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MaintenanceService {

    private final MaintenanceRequestRepository maintenanceRepository;
    private final AssetRepository assetRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;

    public MaintenanceResponse createRequest(MaintenanceRequestDto request, String actorEmail) {
        Employee actor = employeeRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", actorEmail));

        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset", request.getAssetId()));

        MaintenanceRequest mr = MaintenanceRequest.builder()
                .asset(asset)
                .reportedBy(actor)
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(MaintenanceStatus.PENDING)
                .build();

        mr = maintenanceRepository.save(mr);

        activityLogService.log(actor, "MAINTENANCE_CREATED", "Reported issue for asset " + asset.getAssetTag());
        notificationService.sendNotification(actor, "Maintenance request raised for " + asset.getAssetName() + " (" + asset.getAssetTag() + ").", "MAINTENANCE");

        return mapToResponse(mr);
    }

    public MaintenanceResponse updateRequest(Long id, MaintenanceUpdateDto request, String actorEmail) {
        Employee actor = employeeRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", actorEmail));

        MaintenanceRequest mr = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceRequest", id));

        MaintenanceStatus oldStatus = mr.getStatus();
        MaintenanceStatus newStatus = request.getStatus();

        if (newStatus != null) {
            mr.setStatus(newStatus);
            // Handle asset status auto-syncs
            Asset asset = mr.getAsset();
            if (newStatus == MaintenanceStatus.APPROVED && oldStatus != MaintenanceStatus.APPROVED) {
                asset.setStatus(AssetStatus.UNDER_MAINTENANCE);
                assetRepository.save(asset);
                notificationService.sendNotification(mr.getReportedBy(), "Maintenance request approved for " + asset.getAssetName() + ". Asset status is now Under Maintenance.", "MAINTENANCE_APPROVED");
            } else if (newStatus == MaintenanceStatus.RESOLVED && oldStatus != MaintenanceStatus.RESOLVED) {
                asset.setStatus(AssetStatus.AVAILABLE);
                assetRepository.save(asset);
                notificationService.sendNotification(mr.getReportedBy(), "Maintenance request resolved for " + asset.getAssetName() + ". Asset status is now Available.", "MAINTENANCE_RESOLVED");
            }
        }

        if (request.getTechnicianName() != null) {
            mr.setTechnicianName(request.getTechnicianName());
        }

        if (request.getNotes() != null) {
            mr.setNotes(request.getNotes());
        }

        mr = maintenanceRepository.save(mr);

        activityLogService.log(actor, "MAINTENANCE_UPDATED", "Updated maintenance request " + id + " to status " + mr.getStatus());

        return mapToResponse(mr);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceResponse> getAllRequests() {
        return maintenanceRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MaintenanceResponse> getRequestsByAsset(Long assetId) {
        return maintenanceRepository.findByAssetIdOrderByCreatedAtDesc(assetId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private MaintenanceResponse mapToResponse(MaintenanceRequest mr) {
        return MaintenanceResponse.builder()
                .id(mr.getId())
                .assetId(mr.getAsset().getId())
                .assetTag(mr.getAsset().getAssetTag())
                .assetName(mr.getAsset().getAssetName())
                .reportedById(mr.getReportedBy().getId())
                .reportedByName(mr.getReportedBy().getFullName())
                .description(mr.getDescription())
                .priority(mr.getPriority())
                .status(mr.getStatus())
                .technicianName(mr.getTechnicianName())
                .notes(mr.getNotes())
                .createdAt(mr.getCreatedAt())
                .updatedAt(mr.getUpdatedAt())
                .build();
    }
}
