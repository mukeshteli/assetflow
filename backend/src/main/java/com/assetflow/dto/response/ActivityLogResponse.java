package com.assetflow.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLogResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private String action;
    private String details;
    private LocalDateTime createdAt;
}
