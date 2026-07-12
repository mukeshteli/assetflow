package com.assetflow.service;

import com.assetflow.dto.request.AssetRequest;
import com.assetflow.dto.response.AssetResponse;
import com.assetflow.entity.AssetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AssetService {

    /**
     * Register a new asset.
     */
    AssetResponse createAsset(AssetRequest request);

    /**
     * Update an existing asset.
     */
    AssetResponse updateAsset(Long assetId, AssetRequest request);

    /**
     * Delete an asset.
     */
    void deleteAsset(Long assetId);

    /**
     * Get asset by ID.
     */
    AssetResponse getAssetById(Long assetId);

    /**
     * Get all assets with pagination.
     */
    Page<AssetResponse> getAllAssets(Pageable pageable);

    /**
     * Change asset status.
     */
    AssetResponse changeAssetStatus(Long assetId, AssetStatus status);

    /**
     * Search asset by Asset Tag.
     */
    AssetResponse getAssetByTag(String assetTag);
    Page<AssetResponse> searchAssets(
            String keyword,
            Pageable pageable
    );
    Page<AssetResponse> filterAssets(
            String keyword,
            Long departmentId,
            Long categoryId,
            AssetStatus status,
            Pageable pageable
    );

}