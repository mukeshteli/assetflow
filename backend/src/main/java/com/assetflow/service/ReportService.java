package com.assetflow.service;

import com.assetflow.dto.response.AnalyticsResponse;
import com.assetflow.dto.response.DashboardKpisResponse;
import com.assetflow.entity.*;
import com.assetflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final AssetRepository assetRepository;
    private final AllocationRepository allocationRepository;
    private final ResourceBookingRepository bookingRepository;
    private final MaintenanceRequestRepository maintenanceRepository;
    private final TransferRequestRepository transferRepository;
    private final DepartmentRepository departmentRepository;

    public DashboardKpisResponse getDashboardKpis() {
        long totalAssets = assetRepository.count();
        long available = assetRepository.findAll().stream().filter(a -> a.getStatus() == AssetStatus.AVAILABLE).count();
        long allocated = assetRepository.findAll().stream().filter(a -> a.getStatus() == AssetStatus.ALLOCATED).count();

        long activeBookings = bookingRepository.findByStatusIn(List.of(BookingStatus.UPCOMING, BookingStatus.ONGOING)).size();
        long pendingTransfers = transferRepository.findByStatus(TransferRequestStatus.PENDING).size();
        long maintenanceCount = maintenanceRepository.findByStatus(MaintenanceStatus.PENDING).size() +
                                maintenanceRepository.findByStatus(MaintenanceStatus.APPROVED).size() +
                                maintenanceRepository.findByStatus(MaintenanceStatus.IN_PROGRESS).size();

        // Calculate upcoming returns (return date within next 7 days, excluding overdue)
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);
        long upcomingReturns = allocationRepository.findByStatus(AllocationStatus.ACTIVE).stream()
                .filter(a -> a.getExpectedReturnDate() != null &&
                             !a.getExpectedReturnDate().isBefore(today) &&
                             !a.getExpectedReturnDate().isAfter(nextWeek))
                .count();

        // Calculate overdue count (expected return date < today, returnedAt is null)
        long overdueCount = allocationRepository.findOverdueAllocations(AllocationStatus.ACTIVE, today).size();

        return DashboardKpisResponse.builder()
                .availableAssets(available)
                .allocatedAssets(allocated)
                .activeBookings(activeBookings)
                .pendingTransfers(pendingTransfers)
                .upcomingReturns(upcomingReturns)
                .maintenanceCount(maintenanceCount)
                .overdueCount(overdueCount)
                .build();
    }

    public AnalyticsResponse getAnalyticsReport() {
        List<Asset> allAssets = assetRepository.findAll();
        long totalAssets = allAssets.size();

        // 1. Department Utilizations
        List<Department> departments = departmentRepository.findAll();
        List<Allocation> activeAllocations = allocationRepository.findByStatus(AllocationStatus.ACTIVE);

        List<AnalyticsResponse.DepartmentUtilizationDto> utils = departments.stream().map(dept -> {
            long count = activeAllocations.stream()
                    .filter(a -> (a.getDepartment() != null && a.getDepartment().getId().equals(dept.getId())) ||
                                 (a.getEmployee() != null && a.getEmployee().getDepartment() != null && a.getEmployee().getDepartment().getId().equals(dept.getId())))
                    .count();

            double percentage = totalAssets > 0 ? ((double) count / totalAssets) * 100 : 0.0;

            return AnalyticsResponse.DepartmentUtilizationDto.builder()
                    .departmentName(dept.getName())
                    .allocatedCount(count)
                    .totalCount(totalAssets)
                    .utilizationPercentage(Math.round(percentage * 10.0) / 10.0)
                    .build();
        }).toList();

        // 2. Category Maintenance frequency
        List<MaintenanceRequest> maintenanceRequests = maintenanceRepository.findAll();
        Map<String, Long> categoryMaintMap = maintenanceRequests.stream()
                .collect(Collectors.groupingBy(mr -> mr.getAsset().getCategory().getName(), Collectors.counting()));

        List<AnalyticsResponse.CategoryMaintenanceDto> maints = categoryMaintMap.entrySet().stream()
                .map(e -> AnalyticsResponse.CategoryMaintenanceDto.builder()
                        .categoryName(e.getKey())
                        .maintenanceCount(e.getValue())
                        .build())
                .toList();

        // 3. Most-used vs Idle assets
        // usage = count of allocations + bookings
        List<Allocation> allAllocations = allocationRepository.findAll();
        List<ResourceBooking> allBookings = bookingRepository.findAll();

        Map<Long, Long> assetUsageCounts = new HashMap<>();
        for (Asset asset : allAssets) {
            long allocs = allAllocations.stream().filter(a -> a.getAsset().getId().equals(asset.getId())).count();
            long bookings = allBookings.stream().filter(b -> b.getAsset().getId().equals(asset.getId())).count();
            assetUsageCounts.put(asset.getId(), allocs + bookings);
        }

        List<AnalyticsResponse.AssetUsageDto> mostUsed = allAssets.stream()
                .map(a -> AnalyticsResponse.AssetUsageDto.builder()
                        .assetTag(a.getAssetTag())
                        .assetName(a.getAssetName())
                        .usageCount(assetUsageCounts.getOrDefault(a.getId(), 0L))
                        .status(a.getStatus().name())
                        .build())
                .sorted(Comparator.comparingLong(AnalyticsResponse.AssetUsageDto::getUsageCount).reversed())
                .limit(5)
                .toList();

        List<AnalyticsResponse.AssetUsageDto> idle = allAssets.stream()
                .map(a -> AnalyticsResponse.AssetUsageDto.builder()
                        .assetTag(a.getAssetTag())
                        .assetName(a.getAssetName())
                        .usageCount(assetUsageCounts.getOrDefault(a.getId(), 0L))
                        .status(a.getStatus().name())
                        .build())
                .filter(dto -> dto.getUsageCount() == 0 && dto.getStatus().equals("AVAILABLE"))
                .limit(5)
                .toList();

        // 4. Near Retirement
        LocalDate today = LocalDate.now();
        List<AnalyticsResponse.NearRetirementAssetDto> nearRetirement = allAssets.stream()
                .filter(a -> a.getCondition() == AssetCondition.POOR ||
                             a.getCondition() == AssetCondition.DAMAGED ||
                             (a.getWarrantyExpiry() != null && a.getWarrantyExpiry().isBefore(today.plusMonths(3))))
                .map(a -> AnalyticsResponse.NearRetirementAssetDto.builder()
                        .assetTag(a.getAssetTag())
                        .assetName(a.getAssetName())
                        .condition(a.getCondition().name())
                        .status(a.getStatus().name())
                        .warrantyExpiry(a.getWarrantyExpiry())
                        .build())
                .toList();

        // 5. Booking Heatmap hourly slots (9-10, 10-11, 11-12, 12-13)
        Map<String, Long> heatmapMap = new LinkedHashMap<>();
        heatmapMap.put("09:00 - 10:00", 0L);
        heatmapMap.put("10:00 - 11:00", 0L);
        heatmapMap.put("11:00 - 12:00", 0L);
        heatmapMap.put("12:00 - 13:00", 0L);

        for (ResourceBooking booking : allBookings) {
            if (booking.getStatus() == BookingStatus.CANCELLED) continue;
            LocalTime start = booking.getStartTime().toLocalTime();
            if (start.isBefore(LocalTime.of(10, 0)) && !start.isBefore(LocalTime.of(9, 0))) {
                heatmapMap.put("09:00 - 10:00", heatmapMap.get("09:00 - 10:00") + 1);
            } else if (start.isBefore(LocalTime.of(11, 0)) && !start.isBefore(LocalTime.of(10, 0))) {
                heatmapMap.put("10:00 - 11:00", heatmapMap.get("10:00 - 11:00") + 1);
            } else if (start.isBefore(LocalTime.of(12, 0)) && !start.isBefore(LocalTime.of(11, 0))) {
                heatmapMap.put("11:00 - 12:00", heatmapMap.get("11:00 - 12:00") + 1);
            } else if (start.isBefore(LocalTime.of(13, 0)) && !start.isBefore(LocalTime.of(12, 0))) {
                heatmapMap.put("12:00 - 13:00", heatmapMap.get("12:00 - 13:00") + 1);
            }
        }

        List<AnalyticsResponse.BookingHeatmapDto> heatmap = heatmapMap.entrySet().stream()
                .map(e -> AnalyticsResponse.BookingHeatmapDto.builder()
                        .hourSlot(e.getKey())
                        .bookingCount(e.getValue())
                        .build())
                .toList();

        return AnalyticsResponse.builder()
                .departmentUtilizations(utils)
                .categoryMaintenances(maints)
                .mostUsedAssets(mostUsed)
                .idleAssets(idle)
                .nearRetirementAssets(nearRetirement)
                .bookingHeatmap(heatmap)
                .build();
    }
}
