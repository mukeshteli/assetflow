package com.assetflow.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsResponse {

    private List<DepartmentUtilizationDto> departmentUtilizations;
    private List<CategoryMaintenanceDto> categoryMaintenances;
    private List<AssetUsageDto> mostUsedAssets;
    private List<AssetUsageDto> idleAssets;
    private List<NearRetirementAssetDto> nearRetirementAssets;
    private List<BookingHeatmapDto> bookingHeatmap;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DepartmentUtilizationDto {
        private String departmentName;
        private long allocatedCount;
        private long totalCount;
        private double utilizationPercentage;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryMaintenanceDto {
        private String categoryName;
        private long maintenanceCount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssetUsageDto {
        private String assetTag;
        private String assetName;
        private long usageCount;
        private String status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NearRetirementAssetDto {
        private String assetTag;
        private String assetName;
        private String condition;
        private String status;
        private LocalDate warrantyExpiry;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookingHeatmapDto {
        private String hourSlot;
        private long bookingCount;
    }
}
