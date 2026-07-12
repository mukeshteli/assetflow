package com.assetflow.dto.response;

import com.assetflow.entity.MaintenancePriority;
import com.assetflow.entity.MaintenanceStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceResponse {

    private Long id;
    private Long assetId;
    private String assetTag;
    private String assetName;
    private Long reportedById;
    private String reportedByName;
    private String description;
    private MaintenancePriority priority;
    private MaintenanceStatus status;
    private String technicianName;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
