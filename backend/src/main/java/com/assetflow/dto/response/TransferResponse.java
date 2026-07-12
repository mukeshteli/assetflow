package com.assetflow.dto.response;

import com.assetflow.entity.TransferRequestStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferResponse {

    private Long id;
    private Long assetId;
    private String assetTag;
    private String assetName;
    private Long requestedById;
    private String requestedByName;
    private Long fromEmployeeId;
    private String fromEmployeeName;
    private Long toEmployeeId;
    private String toEmployeeName;
    private TransferRequestStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long actionedById;
    private String actionedByName;
    private LocalDateTime actionedAt;
}
