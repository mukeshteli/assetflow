package com.assetflow.repository;

import com.assetflow.entity.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long>, JpaSpecificationExecutor<Asset> {

    boolean existsByAssetTag(String assetTag);

    boolean existsBySerialNumber(String serialNumber);

    Optional<Asset> findByAssetTag(String assetTag);

    Page<Asset> findByAssetNameContainingIgnoreCaseOrAssetTagContainingIgnoreCaseOrSerialNumberContainingIgnoreCase(
            String assetName,
            String assetTag,
            String serialNumber,
            Pageable pageable
    );

}