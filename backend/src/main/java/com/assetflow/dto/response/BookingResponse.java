package com.assetflow.dto.response;

import com.assetflow.entity.BookingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {

    private Long id;
    private Long assetId;
    private String assetTag;
    private String assetName;
    private Long bookedById;
    private String bookedByName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;
    private String purpose;
    private LocalDateTime createdAt;
}
