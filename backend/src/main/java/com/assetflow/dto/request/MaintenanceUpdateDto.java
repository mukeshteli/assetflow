package com.assetflow.dto.request;

import com.assetflow.entity.MaintenanceStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceUpdateDto {

    private MaintenanceStatus status;
    private String technicianName;
    private String notes;
}
