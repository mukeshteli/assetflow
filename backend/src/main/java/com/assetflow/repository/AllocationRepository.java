package com.assetflow.repository;

import com.assetflow.entity.Allocation;
import com.assetflow.entity.AllocationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AllocationRepository extends JpaRepository<Allocation, Long> {

    Optional<Allocation> findFirstByAssetIdAndStatusOrderByAllocatedAtDesc(Long assetId, AllocationStatus status);

    List<Allocation> findByStatus(AllocationStatus status);

    @Query("SELECT a FROM Allocation a WHERE a.status = :status AND a.expectedReturnDate < :date AND a.returnedAt IS NULL")
    List<Allocation> findOverdueAllocations(@Param("status") AllocationStatus status, @Param("date") LocalDate date);

    List<Allocation> findByAssetIdOrderByAllocatedAtDesc(Long assetId);
}
