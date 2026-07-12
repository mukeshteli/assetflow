package com.assetflow.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferCreateRequest {

    @NotNull(message = "Asset ID is required")
    private Long assetId;

    @NotNull(message = "Destination Employee ID is required")
    private Long toEmployeeId;

    private String notes;
}
