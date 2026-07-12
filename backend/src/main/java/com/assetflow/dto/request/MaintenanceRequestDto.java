package com.assetflow.dto.request;

import com.assetflow.entity.MaintenancePriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceRequestDto {

    @NotNull(message = "Asset ID is required")
    private Long assetId;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Priority is required")
    private MaintenancePriority priority;
}
