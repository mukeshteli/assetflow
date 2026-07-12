package com.assetflow.dto.auth;

import com.assetflow.entity.Role;

public record AuthResponse(
        String token,
        Long employeeId,
        String fullName,
        String email,
        Role role
) {
}