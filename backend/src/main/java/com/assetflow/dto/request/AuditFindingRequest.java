package com.assetflow.dto.request;

import com.assetflow.entity.AuditFindingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditFindingRequest {

    @NotNull(message = "Asset ID is required")
    private Long assetId;

    @NotNull(message = "Status is required")
    private AuditFindingStatus status;

    private String notes;
}
