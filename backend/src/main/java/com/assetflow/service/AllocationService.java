package com.assetflow.service;

import com.assetflow.dto.request.AllocationRequest;
import com.assetflow.dto.request.AllocationReturnRequest;
import com.assetflow.dto.request.TransferCreateRequest;
import com.assetflow.dto.response.AllocationResponse;
import com.assetflow.dto.response.TransferResponse;
import com.assetflow.entity.*;
import com.assetflow.exception.ApiException;
import com.assetflow.exception.ResourceNotFoundException;
import com.assetflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AllocationService {

    private final AllocationRepository allocationRepository;
    private final AssetRepository assetRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final TransferRequestRepository transferRequestRepository;
    private final NotificationService notificationService;
    private final ActivityLogService activityLogService;

    public AllocationResponse allocateAsset(AllocationRequest request, String actorEmail) {
        Employee actor = employeeRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", actorEmail));

        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset", request.getAssetId()));

        // Double allocation conflict check
        Optional<Allocation> activeAllocation = allocationRepository
                .findFirstByAssetIdAndStatusOrderByAllocatedAtDesc(asset.getId(), AllocationStatus.ACTIVE);

        if (activeAllocation.isPresent()) {
            Allocation current = activeAllocation.get();
            String holderName = current.getEmployee() != null ? current.getEmployee().getFullName() : "Department " + current.getDepartment().getName();
            throw new ApiException(
                    "Conflict: Asset is currently held by " + holderName,
                    HttpStatus.CONFLICT
            );
        }

        Employee employee = null;
        if (request.getEmployeeId() != null) {
            employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));
        }

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", request.getDepartmentId()));
        }

        if (employee == null && department == null) {
            throw new ApiException("Either Employee ID or Department ID is required for allocation.", HttpStatus.BAD_REQUEST);
        }

        Allocation allocation = Allocation.builder()
                .asset(asset)
                .employee(employee)
                .department(department)
                .allocatedBy(actor)
                .expectedReturnDate(request.getExpectedReturnDate())
                .status(AllocationStatus.ACTIVE)
                .build();

        allocation = allocationRepository.save(allocation);

        // Update asset status
        asset.setStatus(AssetStatus.ALLOCATED);
        assetRepository.save(asset);

        // Notify and Log
        String targetName = employee != null ? employee.getFullName() : department.getName();
        activityLogService.log(actor, "ALLOCATE_ASSET", "Allocated asset " + asset.getAssetTag() + " to " + targetName);
        if (employee != null) {
            notificationService.sendNotification(employee, "Asset " + asset.getAssetName() + " (" + asset.getAssetTag() + ") has been allocated to you.", "ALLOCATION");
        }

        return mapToResponse(allocation);
    }

    public AllocationResponse returnAsset(Long assetId, AllocationReturnRequest request, String actorEmail) {
        Employee actor = employeeRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", actorEmail));

        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", assetId));

        Allocation active = allocationRepository
                .findFirstByAssetIdAndStatusOrderByAllocatedAtDesc(asset.getId(), AllocationStatus.ACTIVE)
                .orElseThrow(() -> new ApiException("No active allocation found for asset: " + asset.getAssetTag(), HttpStatus.NOT_FOUND));

        active.setStatus(AllocationStatus.RETURNED);
        active.setReturnedAt(LocalDateTime.now());
        active.setReturnNotes(request != null ? request.getReturnNotes() : null);
        allocationRepository.save(active);

        asset.setStatus(AssetStatus.AVAILABLE);
        assetRepository.save(asset);

        activityLogService.log(actor, "RETURN_ASSET", "Asset " + asset.getAssetTag() + " was returned. Notes: " + (request != null ? request.getReturnNotes() : "none"));
        if (active.getEmployee() != null) {
            notificationService.sendNotification(active.getEmployee(), "Asset " + asset.getAssetName() + " (" + asset.getAssetTag() + ") has been marked as returned.", "RETURN");
        }

        return mapToResponse(active);
    }

    public TransferResponse requestTransfer(TransferCreateRequest request, String actorEmail) {
        Employee actor = employeeRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", actorEmail));

        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset", request.getAssetId()));

        Employee targetEmployee = employeeRepository.findById(request.getToEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getToEmployeeId()));

        // Get current holder
        Allocation active = allocationRepository
                .findFirstByAssetIdAndStatusOrderByAllocatedAtDesc(asset.getId(), AllocationStatus.ACTIVE)
                .orElseThrow(() -> new ApiException("Asset must be currently allocated to be transferred.", HttpStatus.BAD_REQUEST));

        TransferRequest transferRequest = TransferRequest.builder()
                .asset(asset)
                .requestedBy(actor)
                .fromEmployee(active.getEmployee())
                .toEmployee(targetEmployee)
                .notes(request.getNotes())
                .status(TransferRequestStatus.PENDING)
                .build();

        transferRequest = transferRequestRepository.save(transferRequest);

        activityLogService.log(actor, "TRANSFER_REQUEST", "Requested transfer of " + asset.getAssetTag() + " to " + targetEmployee.getFullName());
        if (active.getEmployee() != null) {
            notificationService.sendNotification(active.getEmployee(), "A request has been made to transfer your asset " + asset.getAssetName() + " (" + asset.getAssetTag() + ") to " + targetEmployee.getFullName(), "TRANSFER_PENDING");
        }
        notificationService.sendNotification(targetEmployee, "A request has been made to transfer asset " + asset.getAssetName() + " (" + asset.getAssetTag() + ") to you.", "TRANSFER_PENDING");

        return mapTransferToResponse(transferRequest);
    }

    public TransferResponse approveTransfer(Long transferId, String actorEmail) {
        Employee actor = employeeRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", actorEmail));

        TransferRequest tr = transferRequestRepository.findById(transferId)
                .orElseThrow(() -> new ResourceNotFoundException("TransferRequest", transferId));

        if (tr.getStatus() != TransferRequestStatus.PENDING) {
            throw new ApiException("Transfer request is already actioned.", HttpStatus.BAD_REQUEST);
        }

        tr.setStatus(TransferRequestStatus.APPROVED);
        tr.setActionedBy(actor);
        tr.setActionedAt(LocalDateTime.now());
        transferRequestRepository.save(tr);

        // Terminate active allocation
        Allocation active = allocationRepository
                .findFirstByAssetIdAndStatusOrderByAllocatedAtDesc(tr.getAsset().getId(), AllocationStatus.ACTIVE)
                .orElseThrow(() -> new ApiException("No active allocation found to transfer.", HttpStatus.INTERNAL_SERVER_ERROR));
        active.setStatus(AllocationStatus.TRANSFERRED);
        active.setReturnedAt(LocalDateTime.now());
        active.setReturnNotes("Transferred to " + tr.getToEmployee().getFullName());
        allocationRepository.save(active);

        // Allocate to new employee
        Allocation newAlloc = Allocation.builder()
                .asset(tr.getAsset())
                .employee(tr.getToEmployee())
                .allocatedBy(actor)
                .status(AllocationStatus.ACTIVE)
                .build();
        allocationRepository.save(newAlloc);

        // Notify and Log
        activityLogService.log(actor, "TRANSFER_APPROVED", "Approved transfer of " + tr.getAsset().getAssetTag() + " to " + tr.getToEmployee().getFullName());
        if (tr.getFromEmployee() != null) {
            notificationService.sendNotification(tr.getFromEmployee(), "Your transfer of asset " + tr.getAsset().getAssetName() + " to " + tr.getToEmployee().getFullName() + " was approved.", "TRANSFER_APPROVED");
        }
        notificationService.sendNotification(tr.getToEmployee(), "Asset " + tr.getAsset().getAssetName() + " (" + tr.getAsset().getAssetTag() + ") has been transferred to you.", "TRANSFER_APPROVED");

        return mapTransferToResponse(tr);
    }

    public TransferResponse rejectTransfer(Long transferId, String actorEmail) {
        Employee actor = employeeRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", actorEmail));

        TransferRequest tr = transferRequestRepository.findById(transferId)
                .orElseThrow(() -> new ResourceNotFoundException("TransferRequest", transferId));

        if (tr.getStatus() != TransferRequestStatus.PENDING) {
            throw new ApiException("Transfer request is already actioned.", HttpStatus.BAD_REQUEST);
        }

        tr.setStatus(TransferRequestStatus.REJECTED);
        tr.setActionedBy(actor);
        tr.setActionedAt(LocalDateTime.now());
        transferRequestRepository.save(tr);

        activityLogService.log(actor, "TRANSFER_REJECTED", "Rejected transfer of " + tr.getAsset().getAssetTag() + " to " + tr.getToEmployee().getFullName());
        notificationService.sendNotification(tr.getRequestedBy(), "Your transfer request for asset " + tr.getAsset().getAssetName() + " was rejected.", "TRANSFER_REJECTED");

        return mapTransferToResponse(tr);
    }

    @Transactional(readOnly = true)
    public List<AllocationResponse> getAssetHistory(Long assetId) {
        return allocationRepository.findByAssetIdOrderByAllocatedAtDesc(assetId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AllocationResponse> getOverdueReturns() {
        return allocationRepository.findOverdueAllocations(AllocationStatus.ACTIVE, LocalDate.now()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransferResponse> getPendingTransfers() {
        return transferRequestRepository.findByStatus(TransferRequestStatus.PENDING).stream()
                .map(this::mapTransferToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransferResponse> getAllTransfers() {
        return transferRequestRepository.findAll().stream()
                .map(this::mapTransferToResponse)
                .toList();
    }

    private AllocationResponse mapToResponse(Allocation a) {
        return AllocationResponse.builder()
                .id(a.getId())
                .assetId(a.getAsset().getId())
                .assetTag(a.getAsset().getAssetTag())
                .assetName(a.getAsset().getAssetName())
                .employeeId(a.getEmployee() != null ? a.getEmployee().getId() : null)
                .employeeName(a.getEmployee() != null ? a.getEmployee().getFullName() : null)
                .departmentId(a.getDepartment() != null ? a.getDepartment().getId() : null)
                .departmentName(a.getDepartment() != null ? a.getDepartment().getName() : null)
                .allocatedById(a.getAllocatedBy().getId())
                .allocatedByName(a.getAllocatedBy().getFullName())
                .allocatedAt(a.getAllocatedAt())
                .expectedReturnDate(a.getExpectedReturnDate())
                .returnedAt(a.getReturnedAt())
                .returnNotes(a.getReturnNotes())
                .status(a.getStatus())
                .build();
    }

    private TransferResponse mapTransferToResponse(TransferRequest tr) {
        return TransferResponse.builder()
                .id(tr.getId())
                .assetId(tr.getAsset().getId())
                .assetTag(tr.getAsset().getAssetTag())
                .assetName(tr.getAsset().getAssetName())
                .requestedById(tr.getRequestedBy().getId())
                .requestedByName(tr.getRequestedBy().getFullName())
                .fromEmployeeId(tr.getFromEmployee() != null ? tr.getFromEmployee().getId() : null)
                .fromEmployeeName(tr.getFromEmployee() != null ? tr.getFromEmployee().getFullName() : null)
                .toEmployeeId(tr.getToEmployee().getId())
                .toEmployeeName(tr.getToEmployee().getFullName())
                .status(tr.getStatus())
                .notes(tr.getNotes())
                .createdAt(tr.getCreatedAt())
                .updatedAt(tr.getUpdatedAt())
                .actionedById(tr.getActionedBy() != null ? tr.getActionedBy().getId() : null)
                .actionedByName(tr.getActionedBy() != null ? tr.getActionedBy().getFullName() : null)
                .actionedAt(tr.getActionedAt())
                .build();
    }
}
