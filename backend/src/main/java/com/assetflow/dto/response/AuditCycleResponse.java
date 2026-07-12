package com.assetflow.dto.response;

import com.assetflow.entity.AuditCycleStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditCycleResponse {

    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private AuditCycleStatus status;
    private LocalDateTime createdAt;

    private List<String> auditorNames;
    private Long targetDepartmentId;
    private String targetDepartmentName;
    private String targetLocation;

    private long verifiedCount;
    private long missingCount;
    private long damagedCount;
    private long totalAssets;
}
