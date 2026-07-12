package com.assetflow.repository;

import com.assetflow.entity.AssetCategory;
import com.assetflow.entity.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetCategoryRepository extends JpaRepository<AssetCategory, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<AssetCategory> findByNameIgnoreCase(String name);

    List<AssetCategory> findByStatus(EntityStatus status);

}