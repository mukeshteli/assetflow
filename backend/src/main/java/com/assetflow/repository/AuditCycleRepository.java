package com.assetflow.repository;

import com.assetflow.entity.AuditCycle;
import com.assetflow.entity.AuditCycleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditCycleRepository extends JpaRepository<AuditCycle, Long> {

    List<AuditCycle> findByStatus(AuditCycleStatus status);
}
