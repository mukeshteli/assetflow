package com.assetflow.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardKpisResponse {

    private long availableAssets;
    private long allocatedAssets;
    private long activeBookings;
    private long pendingTransfers;
    private long upcomingReturns;
    private long maintenanceCount;
    private long overdueCount;
}
