package com.assetflow.dto.response;

import com.assetflow.entity.EntityStatus;
import com.assetflow.entity.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {

    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private Long departmentId;
    private String departmentName;
    private EntityStatus status;
}
