package com.assetflow.controller;

import com.assetflow.dto.response.AnalyticsResponse;
import com.assetflow.dto.response.DashboardKpisResponse;
import com.assetflow.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard-kpis")
    @PreAuthorize("isAuthenticated()")
    public DashboardKpisResponse getDashboardKpis() {
        return reportService.getDashboardKpis();
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASSET_MANAGER', 'DEPARTMENT_HEAD')")
    public AnalyticsResponse getAnalytics() {
        return reportService.getAnalyticsReport();
    }
}
