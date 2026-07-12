package com.assetflow.controller;

import com.assetflow.dto.request.MaintenanceRequestDto;
import com.assetflow.dto.request.MaintenanceUpdateDto;
import com.assetflow.dto.response.MaintenanceResponse;
import com.assetflow.service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public MaintenanceResponse createRequest(
            @Valid @RequestBody MaintenanceRequestDto request,
            Principal principal
    ) {
        return maintenanceService.createRequest(request, principal.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSET_MANAGER')")
    public MaintenanceResponse updateRequest(
            @PathVariable Long id,
            @Valid @RequestBody MaintenanceUpdateDto request,
            Principal principal
    ) {
        return maintenanceService.updateRequest(id, request, principal.getName());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<MaintenanceResponse> getAllRequests() {
        return maintenanceService.getAllRequests();
    }

    @GetMapping("/asset/{assetId}")
    @PreAuthorize("isAuthenticated()")
    public List<MaintenanceResponse> getRequestsByAsset(
            @PathVariable Long assetId
    ) {
        return maintenanceService.getRequestsByAsset(assetId);
    }
}
