package com.assetflow.service;

import com.assetflow.dto.response.DepartmentResponse;
import com.assetflow.entity.Department;
import com.assetflow.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private DepartmentResponse toResponse(Department department) {
        Department parent = department.getParentDepartment();
        var head = department.getHead();

        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .parentDepartmentId(parent != null ? parent.getId() : null)
                .parentDepartmentName(parent != null ? parent.getName() : null)
                .headEmployeeId(head != null ? head.getId() : null)
                .headEmployeeName(head != null ? head.getFullName() : null)
                .status(department.getStatus())
                .build();
    }
}
