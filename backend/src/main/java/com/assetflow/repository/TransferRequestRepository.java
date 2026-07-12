package com.assetflow.repository;

import com.assetflow.entity.TransferRequest;
import com.assetflow.entity.TransferRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransferRequestRepository extends JpaRepository<TransferRequest, Long> {

    Optional<TransferRequest> findFirstByAssetIdAndStatusOrderByCreatedAtDesc(Long assetId, TransferRequestStatus status);

    List<TransferRequest> findByStatus(TransferRequestStatus status);
}
