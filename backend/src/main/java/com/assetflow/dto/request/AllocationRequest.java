package com.assetflow.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocationRequest {

    @NotNull(message = "Asset ID is required")
    private Long assetId;

    private Long employeeId;

    private Long departmentId;

    private LocalDate expectedReturnDate;
}
