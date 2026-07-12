package com.assetflow.repository;

import com.assetflow.entity.AuditAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditAssignmentRepository extends JpaRepository<AuditAssignment, Long> {

    List<AuditAssignment> findByAuditCycleId(Long auditCycleId);
}
