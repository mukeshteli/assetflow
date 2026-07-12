package com.assetflow.dto.response;

import com.assetflow.entity.EntityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponse {

    private Long id;
    private String name;
    private Long parentDepartmentId;
    private String parentDepartmentName;
    private Long headEmployeeId;
    private String headEmployeeName;
    private EntityStatus status;
}
