package com.assetflow.service;

import com.assetflow.dto.auth.AuthResponse;
import com.assetflow.dto.auth.LoginRequest;
import com.assetflow.dto.auth.SignupRequest;
import com.assetflow.entity.Employee;
import com.assetflow.entity.EntityStatus;
import com.assetflow.entity.Role;
import com.assetflow.exception.DuplicateEmailException;
import com.assetflow.exception.InvalidCredentialsException;
import com.assetflow.repository.EmployeeRepository;
import com.assetflow.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (employeeRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        // Role is intentionally hardcoded here, never taken from the request body.
        // Elevation to Department Head / Asset Manager only happens through the
        // admin-only promote endpoint added in the Organization Setup feature.
        Employee employee = Employee.builder()
                .fullName(request.fullName())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.EMPLOYEE)
                .status(EntityStatus.ACTIVE)
                .build();

        employeeRepository.save(employee);

        return buildAuthResponse(employee);
    }

    public AuthResponse login(LoginRequest request) {
        Employee employee = employeeRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), employee.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        if (employee.getStatus() == EntityStatus.INACTIVE) {
            throw new InvalidCredentialsException();
        }

        return buildAuthResponse(employee);
    }

    private AuthResponse buildAuthResponse(Employee employee) {
        String token = jwtService.generateToken(employee);
        return new AuthResponse(token, employee.getId(), employee.getFullName(), employee.getEmail(), employee.getRole());
    }
}