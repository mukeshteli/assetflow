package com.assetflow.controller;

import com.assetflow.dto.request.DepartmentRequest;
import com.assetflow.dto.response.DepartmentResponse;
import com.assetflow.entity.EntityStatus;
import com.assetflow.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<DepartmentResponse> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public DepartmentResponse createDepartment(
            @Valid @RequestBody DepartmentRequest request,
            Principal principal
    ) {
        return departmentService.createDepartment(request, principal.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DepartmentResponse updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request,
            Principal principal
    ) {
        return departmentService.updateDepartment(id, request, principal.getName());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public DepartmentResponse changeStatus(
            @PathVariable Long id,
            @RequestParam EntityStatus status,
            Principal principal
    ) {
        return departmentService.changeDepartmentStatus(id, status, principal.getName());
    }
}