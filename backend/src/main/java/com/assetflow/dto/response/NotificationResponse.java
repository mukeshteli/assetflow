package com.assetflow.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;
    private Long recipientId;
    private String recipientName;
    private String message;
    private Boolean read;
    private String type;
    private LocalDateTime createdAt;
}
