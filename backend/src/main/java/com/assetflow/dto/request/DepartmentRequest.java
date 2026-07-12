package com.assetflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentRequest {

    @NotBlank(message = "Department name is required")
    private String name;

    private Long parentDepartmentId;

    private Long headEmployeeId;
}
