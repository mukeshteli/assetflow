package com.assetflow.service;

import com.assetflow.dto.response.EmployeeResponse;
import com.assetflow.entity.Employee;
import com.assetflow.entity.EntityStatus;
import com.assetflow.entity.Role;
import com.assetflow.exception.ResourceNotFoundException;
import com.assetflow.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ActivityLogService activityLogService;

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public EmployeeResponse promoteEmployee(Long id, Role role, String actorEmail) {
        Employee actor = employeeRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", actorEmail));

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));

        employee.setRole(role);
        employee = employeeRepository.save(employee);

        activityLogService.log(actor, "PROMOTE_EMPLOYEE", "Promoted employee " + employee.getFullName() + " to " + role.name());

        return mapToResponse(employee);
    }

    public EmployeeResponse changeEmployeeStatus(Long id, EntityStatus status, String actorEmail) {
        Employee actor = employeeRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", actorEmail));

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));

        employee.setStatus(status);
        employee = employeeRepository.save(employee);

        activityLogService.log(actor, "CHANGE_EMPLOYEE_STATUS", "Changed status of " + employee.getFullName() + " to " + status.name());

        return mapToResponse(employee);
    }

    private EmployeeResponse mapToResponse(Employee e) {
        return EmployeeResponse.builder()
                .id(e.getId())
                .fullName(e.getFullName())
                .email(e.getEmail())
                .role(e.getRole())
                .departmentId(e.getDepartment() != null ? e.getDepartment().getId() : null)
                .departmentName(e.getDepartment() != null ? e.getDepartment().getName() : null)
                .status(e.getStatus())
                .build();
    }
}
