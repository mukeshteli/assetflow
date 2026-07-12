package com.assetflow.controller;

import com.assetflow.dto.response.EmployeeResponse;
import com.assetflow.entity.EntityStatus;
import com.assetflow.entity.Role;
import com.assetflow.service.EmployeeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<EmployeeResponse> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse promoteEmployee(
            @PathVariable Long id,
            @RequestParam Role role,
            Principal principal
    ) {
        return employeeService.promoteEmployee(id, role, principal.getName());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse changeStatus(
            @PathVariable Long id,
            @RequestParam EntityStatus status,
            Principal principal
    ) {
        return employeeService.changeEmployeeStatus(id, status, principal.getName());
    }
}
