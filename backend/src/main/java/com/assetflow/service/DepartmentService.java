package com.assetflow.service;

import com.assetflow.dto.request.DepartmentRequest;
import com.assetflow.dto.response.DepartmentResponse;
import com.assetflow.entity.Department;
import com.assetflow.entity.Employee;
import com.assetflow.entity.EntityStatus;
import com.assetflow.exception.ResourceNotFoundException;
import com.assetflow.repository.DepartmentRepository;
import com.assetflow.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final ActivityLogService activityLogService;

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public DepartmentResponse createDepartment(DepartmentRequest request, String actorEmail) {
        Employee actor = employeeRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", actorEmail));

        Department parent = null;
        if (request.getParentDepartmentId() != null) {
            parent = departmentRepository.findById(request.getParentDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", request.getParentDepartmentId()));
        }

        Employee head = null;
        if (request.getHeadEmployeeId() != null) {
            head = employeeRepository.findById(request.getHeadEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getHeadEmployeeId()));
        }

        Department department = Department.builder()
                .name(request.getName())
                .parentDepartment(parent)
                .head(head)
                .status(EntityStatus.ACTIVE)
                .build();

        department = departmentRepository.save(department);

        // Auto-assign employee to department if head
        if (head != null) {
            head.setDepartment(department);
            employeeRepository.save(head);
        }

        activityLogService.log(actor, "CREATE_DEPARTMENT", "Created department " + department.getName());

        return toResponse(department);
    }

    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request, String actorEmail) {
        Employee actor = employeeRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", actorEmail));

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id));

        Department parent = null;
        if (request.getParentDepartmentId() != null) {
            if (request.getParentDepartmentId().equals(id)) {
                throw new IllegalArgumentException("Department cannot be its own parent.");
            }
            parent = departmentRepository.findById(request.getParentDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", request.getParentDepartmentId()));
        }

        Employee head = null;
        if (request.getHeadEmployeeId() != null) {
            head = employeeRepository.findById(request.getHeadEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getHeadEmployeeId()));
        }

        department.setName(request.getName());
        department.setParentDepartment(parent);
        department.setHead(head);

        department = departmentRepository.save(department);

        if (head != null) {
            head.setDepartment(department);
            employeeRepository.save(head);
        }

        activityLogService.log(actor, "UPDATE_DEPARTMENT", "Updated department " + department.getName());

        return toResponse(department);
    }

    public DepartmentResponse changeDepartmentStatus(Long id, EntityStatus status, String actorEmail) {
        Employee actor = employeeRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", actorEmail));

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id));

        department.setStatus(status);
        department = departmentRepository.save(department);

        activityLogService.log(actor, "CHANGE_DEPARTMENT_STATUS", "Changed status of department " + department.getName() + " to " + status.name());

        return toResponse(department);
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
