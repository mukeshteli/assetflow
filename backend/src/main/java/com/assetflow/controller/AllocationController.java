package com.assetflow.controller;

import com.assetflow.dto.request.AllocationRequest;
import com.assetflow.dto.request.AllocationReturnRequest;
import com.assetflow.dto.request.TransferCreateRequest;
import com.assetflow.dto.response.AllocationResponse;
import com.assetflow.dto.response.TransferResponse;
import com.assetflow.service.AllocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/allocations")
@RequiredArgsConstructor
public class AllocationController {

    private final AllocationService allocationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSET_MANAGER')")
    public AllocationResponse allocateAsset(
            @Valid @RequestBody AllocationRequest request,
            Principal principal
    ) {
        return allocationService.allocateAsset(request, principal.getName());
    }

    @PostMapping("/{assetId}/return")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSET_MANAGER')")
    public AllocationResponse returnAsset(
            @PathVariable Long assetId,
            @RequestBody(required = false) AllocationReturnRequest request,
            Principal principal
    ) {
        return allocationService.returnAsset(assetId, request, principal.getName());
    }

    @GetMapping("/history/{assetId}")
    @PreAuthorize("isAuthenticated()")
    public List<AllocationResponse> getHistory(
            @PathVariable Long assetId
    ) {
        return allocationService.getAssetHistory(assetId);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSET_MANAGER', 'DEPARTMENT_HEAD')")
    public List<AllocationResponse> getOverdue() {
        return allocationService.getOverdueReturns();
    }

    @PostMapping("/transfers")
    @PreAuthorize("isAuthenticated()")
    public TransferResponse requestTransfer(
            @Valid @RequestBody TransferCreateRequest request,
            Principal principal
    ) {
        return allocationService.requestTransfer(request, principal.getName());
    }

    @GetMapping("/transfers/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSET_MANAGER', 'DEPARTMENT_HEAD')")
    public List<TransferResponse> getPendingTransfers() {
        return allocationService.getPendingTransfers();
    }

    @GetMapping("/transfers")
    @PreAuthorize("isAuthenticated()")
    public List<TransferResponse> getAllTransfers() {
        return allocationService.getAllTransfers();
    }

    @PostMapping("/transfers/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSET_MANAGER', 'DEPARTMENT_HEAD')")
    public TransferResponse approveTransfer(
            @PathVariable Long id,
            Principal principal
    ) {
        return allocationService.approveTransfer(id, principal.getName());
    }

    @PostMapping("/transfers/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSET_MANAGER', 'DEPARTMENT_HEAD')")
    public TransferResponse rejectTransfer(
            @PathVariable Long id,
            Principal principal
    ) {
        return allocationService.rejectTransfer(id, principal.getName());
    }
}
