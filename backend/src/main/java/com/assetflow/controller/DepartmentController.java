package com.assetflow.controller;

import com.assetflow.dto.response.DepartmentResponse;
import com.assetflow.service.DepartmentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}