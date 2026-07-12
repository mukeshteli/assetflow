package com.assetflow.repository;

import com.assetflow.entity.AuditFinding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuditFindingRepository extends JpaRepository<AuditFinding, Long> {

    List<AuditFinding> findByAuditCycleId(Long auditCycleId);

    Optional<AuditFinding> findByAuditCycleIdAndAssetId(Long auditCycleId, Long assetId);
}
