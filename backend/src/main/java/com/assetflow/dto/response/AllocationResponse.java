package com.assetflow.dto.response;

import com.assetflow.entity.AllocationStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocationResponse {

    private Long id;
    private Long assetId;
    private String assetTag;
    private String assetName;
    private Long employeeId;
    private String employeeName;
    private Long departmentId;
    private String departmentName;
    private Long allocatedById;
    private String allocatedByName;
    private LocalDateTime allocatedAt;
    private LocalDate expectedReturnDate;
    private LocalDateTime returnedAt;
    private String returnNotes;
    private AllocationStatus status;
}
