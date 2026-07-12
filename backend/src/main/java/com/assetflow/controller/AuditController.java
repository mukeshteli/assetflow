package com.assetflow.controller;

import com.assetflow.dto.request.AuditCycleRequest;
import com.assetflow.dto.request.AuditFindingRequest;
import com.assetflow.dto.response.AuditCycleResponse;
import com.assetflow.dto.response.AuditFindingResponse;
import com.assetflow.service.AuditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/audits")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public AuditCycleResponse createCycle(
            @Valid @RequestBody AuditCycleRequest request,
            Principal principal
    ) {
        return auditService.createCycle(request, principal.getName());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<AuditCycleResponse> getAllCycles() {
        return auditService.getAllCycles();
    }

    @GetMapping("/{cycleId}/checklist")
    @PreAuthorize("isAuthenticated()")
    public List<AuditFindingResponse> getCycleChecklist(
            @PathVariable Long cycleId
    ) {
        return auditService.getCycleChecklist(cycleId);
    }

    @PostMapping("/{cycleId}/findings")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSET_MANAGER')")
    public AuditFindingResponse submitFinding(
            @PathVariable Long cycleId,
            @Valid @RequestBody AuditFindingRequest request,
            Principal principal
    ) {
        return auditService.submitFinding(cycleId, request, principal.getName());
    }

    @PostMapping("/{cycleId}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public AuditCycleResponse closeCycle(
            @PathVariable Long cycleId,
            Principal principal
    ) {
        return auditService.closeCycle(cycleId, principal.getName());
    }
}
