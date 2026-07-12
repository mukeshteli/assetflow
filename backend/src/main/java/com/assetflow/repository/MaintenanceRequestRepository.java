package com.assetflow.repository;

import com.assetflow.entity.MaintenanceRequest;
import com.assetflow.entity.MaintenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {

    List<MaintenanceRequest> findByStatus(MaintenanceStatus status);

    List<MaintenanceRequest> findByAssetIdOrderByCreatedAtDesc(Long assetId);
}
