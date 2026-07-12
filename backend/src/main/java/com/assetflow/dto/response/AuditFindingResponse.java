package com.assetflow.dto.response;

import com.assetflow.entity.AuditFindingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditFindingResponse {

    private Long id;
    private Long auditCycleId;
    private Long assetId;
    private String assetTag;
    private String assetName;
    private String expectedLocation;
    private Long auditedById;
    private String auditedByName;
    private AuditFindingStatus status;
    private String notes;
    private LocalDateTime auditedAt;
}
