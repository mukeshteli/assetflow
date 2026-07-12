package com.assetflow.service;

import com.assetflow.dto.request.AuditCycleRequest;
import com.assetflow.dto.request.AuditFindingRequest;
import com.assetflow.dto.response.AuditCycleResponse;
import com.assetflow.dto.response.AuditFindingResponse;
import com.assetflow.entity.*;
import com.assetflow.exception.ApiException;
import com.assetflow.exception.ResourceNotFoundException;
import com.assetflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditService {

    private final AuditCycleRepository auditCycleRepository;
    private final AuditAssignmentRepository auditAssignmentRepository;
    private final AuditFindingRepository auditFindingRepository;
    private final AssetRepository assetRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final AllocationRepository allocationRepository;
    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;

    public AuditCycleResponse createCycle(AuditCycleRequest request, String actorEmail) {
        Employee actor = employeeRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", actorEmail));

        Department targetDept = null;
        if (request.getTargetDepartmentId() != null) {
            targetDept = departmentRepository.findById(request.getTargetDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", request.getTargetDepartmentId()));
        }

        AuditCycle cycle = AuditCycle.builder()
                .name(request.getName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(AuditCycleStatus.ACTIVE)
                .build();

        cycle = auditCycleRepository.save(cycle);

        List<String> auditorNames = new ArrayList<>();
        for (Long auditorId : request.getAuditorIds()) {
            Employee auditor = employeeRepository.findById(auditorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", auditorId));

            AuditAssignment assignment = AuditAssignment.builder()
                    .auditCycle(cycle)
                    .auditor(auditor)
                    .targetDepartment(targetDept)
                    .targetLocation(request.getTargetLocation())
                    .build();

            auditAssignmentRepository.save(assignment);
            auditorNames.add(auditor.getFullName());

            notificationService.sendNotification(auditor, "You have been assigned as an auditor for audit cycle: " + cycle.getName(), "AUDIT_ASSIGNED");
        }

        activityLogService.log(actor, "AUDIT_CYCLE_CREATED", "Created audit cycle " + cycle.getName());

        return mapCycleToResponse(cycle, auditorNames, 0, 0, 0, getAssetsInScope(targetDept, request.getTargetLocation()).size());
    }

    @Transactional(readOnly = true)
    public List<AuditFindingResponse> getCycleChecklist(Long cycleId) {
        AuditCycle cycle = auditCycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("AuditCycle", cycleId));

        // Get assignments to determine scope
        List<AuditAssignment> assignments = auditAssignmentRepository.findByAuditCycleId(cycleId);
        Department targetDept = assignments.stream().map(AuditAssignment::getTargetDepartment).filter(java.util.Objects::nonNull).findFirst().orElse(null);
        String targetLocation = assignments.stream().map(AuditAssignment::getTargetLocation).filter(java.util.Objects::nonNull).findFirst().orElse(null);

        List<Asset> assets = getAssetsInScope(targetDept, targetLocation);
        List<AuditFindingResponse> checklist = new ArrayList<>();

        for (Asset asset : assets) {
            Optional<AuditFinding> findingOpt = auditFindingRepository.findByAuditCycleIdAndAssetId(cycleId, asset.getId());
            String expectedLocation = asset.getCurrentLocation();

            if (findingOpt.isPresent()) {
                AuditFinding f = findingOpt.get();
                checklist.add(mapFindingToResponse(f, expectedLocation));
            } else {
                checklist.add(AuditFindingResponse.builder()
                        .auditCycleId(cycleId)
                        .assetId(asset.getId())
                        .assetTag(asset.getAssetTag())
                        .assetName(asset.getAssetName())
                        .expectedLocation(expectedLocation)
                        .status(null) // Not audited yet
                        .build());
            }
        }

        return checklist;
    }

    public AuditFindingResponse submitFinding(Long cycleId, AuditFindingRequest request, String actorEmail) {
        Employee actor = employeeRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", actorEmail));

        AuditCycle cycle = auditCycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("AuditCycle", cycleId));

        if (cycle.getStatus() != AuditCycleStatus.ACTIVE) {
            throw new ApiException("Audit cycle is closed.", HttpStatus.BAD_REQUEST);
        }

        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset", request.getAssetId()));

        Optional<AuditFinding> existingOpt = auditFindingRepository.findByAuditCycleIdAndAssetId(cycleId, asset.getId());
        AuditFinding finding;

        if (existingOpt.isPresent()) {
            finding = existingOpt.get();
            finding.setStatus(request.getStatus());
            finding.setNotes(request.getNotes());
            finding.setAuditedBy(actor);
            finding.setAuditedAt(LocalDateTime.now());
        } else {
            finding = AuditFinding.builder()
                    .auditCycle(cycle)
                    .asset(asset)
                    .auditedBy(actor)
                    .status(request.getStatus())
                    .notes(request.getNotes())
                    .build();
        }

        finding = auditFindingRepository.save(finding);

        activityLogService.log(actor, "AUDIT_FINDING", "Submitted finding " + request.getStatus() + " for asset " + asset.getAssetTag());

        return mapFindingToResponse(finding, asset.getCurrentLocation());
    }

    public AuditCycleResponse closeCycle(Long cycleId, String actorEmail) {
        Employee actor = employeeRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", actorEmail));

        AuditCycle cycle = auditCycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("AuditCycle", cycleId));

        if (cycle.getStatus() != AuditCycleStatus.ACTIVE) {
            throw new ApiException("Audit cycle is already closed.", HttpStatus.BAD_REQUEST);
        }

        cycle.setStatus(AuditCycleStatus.CLOSED);
        auditCycleRepository.save(cycle);

        List<AuditFinding> findings = auditFindingRepository.findByAuditCycleId(cycleId);
        long missing = 0;
        long damaged = 0;
        long verified = 0;

        for (AuditFinding finding : findings) {
            Asset asset = finding.getAsset();
            if (finding.getStatus() == AuditFindingStatus.MISSING) {
                asset.setStatus(AssetStatus.LOST);
                assetRepository.save(asset);
                missing++;
                // Notify asset manager
                notificationService.sendNotification(actor, "Asset " + asset.getAssetName() + " (" + asset.getAssetTag() + ") marked as LOST during audit.", "AUDIT_DISCREPANCY");
            } else if (finding.getStatus() == AuditFindingStatus.DAMAGED) {
                asset.setCondition(AssetCondition.DAMAGED);
                assetRepository.save(asset);
                damaged++;
                notificationService.sendNotification(actor, "Asset " + asset.getAssetName() + " (" + asset.getAssetTag() + ") marked as DAMAGED during audit.", "AUDIT_DISCREPANCY");
            } else if (finding.getStatus() == AuditFindingStatus.VERIFIED) {
                verified++;
            }
        }

        activityLogService.log(actor, "AUDIT_CYCLE_CLOSED", "Closed audit cycle " + cycle.getName() + ". Findings: Verified=" + verified + ", Missing=" + missing + ", Damaged=" + damaged);

        // Fetch auditor names for response
        List<AuditAssignment> assignments = auditAssignmentRepository.findByAuditCycleId(cycleId);
        List<String> auditors = assignments.stream().map(a -> a.getAuditor().getFullName()).toList();
        Department targetDept = assignments.stream().map(AuditAssignment::getTargetDepartment).filter(java.util.Objects::nonNull).findFirst().orElse(null);
        String targetLocation = assignments.stream().map(AuditAssignment::getTargetLocation).filter(java.util.Objects::nonNull).findFirst().orElse(null);

        return mapCycleToResponse(cycle, auditors, verified, missing, damaged, getAssetsInScope(targetDept, targetLocation).size());
    }

    @Transactional(readOnly = true)
    public List<AuditCycleResponse> getAllCycles() {
        return auditCycleRepository.findAll().stream()
                .map(cycle -> {
                    List<AuditAssignment> assignments = auditAssignmentRepository.findByAuditCycleId(cycle.getId());
                    List<String> auditors = assignments.stream().map(a -> a.getAuditor().getFullName()).toList();
                    Department targetDept = assignments.stream().map(AuditAssignment::getTargetDepartment).filter(java.util.Objects::nonNull).findFirst().orElse(null);
                    String targetLocation = assignments.stream().map(AuditAssignment::getTargetLocation).filter(java.util.Objects::nonNull).findFirst().orElse(null);

                    List<AuditFinding> findings = auditFindingRepository.findByAuditCycleId(cycle.getId());
                    long verified = findings.stream().filter(f -> f.getStatus() == AuditFindingStatus.VERIFIED).count();
                    long missing = findings.stream().filter(f -> f.getStatus() == AuditFindingStatus.MISSING).count();
                    long damaged = findings.stream().filter(f -> f.getStatus() == AuditFindingStatus.DAMAGED).count();

                    return mapCycleToResponse(cycle, auditors, verified, missing, damaged, getAssetsInScope(targetDept, targetLocation).size());
                })
                .toList();
    }

    private List<Asset> getAssetsInScope(Department targetDept, String targetLocation) {
        List<Asset> allAssets = assetRepository.findAll();
        if (targetDept == null && (targetLocation == null || targetLocation.isBlank())) {
            return allAssets;
        }

        return allAssets.stream()
                .filter(asset -> {
                    boolean deptMatch = true;
                    if (targetDept != null) {
                        // Check if asset is actively allocated to the department OR to an employee belonging to the department
                        Optional<Allocation> activeOpt = allocationRepository.findFirstByAssetIdAndStatusOrderByAllocatedAtDesc(asset.getId(), AllocationStatus.ACTIVE);
                        if (activeOpt.isPresent()) {
                            Allocation alloc = activeOpt.get();
                            boolean directMatch = alloc.getDepartment() != null && alloc.getDepartment().getId().equals(targetDept.getId());
                            boolean employeeMatch = alloc.getEmployee() != null && alloc.getEmployee().getDepartment() != null && alloc.getEmployee().getDepartment().getId().equals(targetDept.getId());
                            deptMatch = directMatch || employeeMatch;
                        } else {
                            deptMatch = false;
                        }
                    }

                    boolean locMatch = true;
                    if (targetLocation != null && !targetLocation.isBlank()) {
                        locMatch = asset.getCurrentLocation() != null && asset.getCurrentLocation().equalsIgnoreCase(targetLocation);
                    }

                    return deptMatch && locMatch;
                })
                .toList();
    }

    private AuditCycleResponse mapCycleToResponse(AuditCycle cycle, List<String> auditors, long verified, long missing, long damaged, int total) {
        List<AuditAssignment> assignments = auditAssignmentRepository.findByAuditCycleId(cycle.getId());
        Department targetDept = assignments.stream().map(AuditAssignment::getTargetDepartment).filter(java.util.Objects::nonNull).findFirst().orElse(null);
        String targetLocation = assignments.stream().map(AuditAssignment::getTargetLocation).filter(java.util.Objects::nonNull).findFirst().orElse(null);

        return AuditCycleResponse.builder()
                .id(cycle.getId())
                .name(cycle.getName())
                .startDate(cycle.getStartDate())
                .endDate(cycle.getEndDate())
                .status(cycle.getStatus())
                .createdAt(cycle.getCreatedAt())
                .auditorNames(auditors)
                .targetDepartmentId(targetDept != null ? targetDept.getId() : null)
                .targetDepartmentName(targetDept != null ? targetDept.getName() : null)
                .targetLocation(targetLocation)
                .verifiedCount(verified)
                .missingCount(missing)
                .damagedCount(damaged)
                .totalAssets(total)
                .build();
    }

    private AuditFindingResponse mapFindingToResponse(AuditFinding f, String expectedLocation) {
        return AuditFindingResponse.builder()
                .id(f.getId())
                .auditCycleId(f.getAuditCycle().getId())
                .assetId(f.getAsset().getId())
                .assetTag(f.getAsset().getAssetTag())
                .assetName(f.getAsset().getAssetName())
                .expectedLocation(expectedLocation)
                .auditedById(f.getAuditedBy().getId())
                .auditedByName(f.getAuditedBy().getFullName())
                .status(f.getStatus())
                .notes(f.getNotes())
                .auditedAt(f.getAuditedAt())
                .build();
    }
}
