package com.assetflow.service.impl;

import com.assetflow.dto.request.AssetRequest;
import com.assetflow.dto.response.AssetResponse;
import com.assetflow.entity.Asset;
import com.assetflow.entity.AssetCategory;
import com.assetflow.entity.AssetStatus;
import com.assetflow.exception.ApiException;
import com.assetflow.exception.ResourceNotFoundException;
import com.assetflow.repository.AssetCategoryRepository;
import com.assetflow.repository.AssetRepository;
import com.assetflow.service.AssetLifecyclePolicy;
import com.assetflow.service.AssetService;
import com.assetflow.specification.AssetSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AssetServiceImpl implements AssetService {

    private static final String TAG_PREFIX = "AF-";

    private final AssetRepository assetRepository;
    private final AssetCategoryRepository assetCategoryRepository;
    private final AssetLifecyclePolicy lifecyclePolicy;

    @Override
    public AssetResponse createAsset(AssetRequest request) {
        if (assetRepository.existsBySerialNumber(request.getSerialNumber())) {
            throw new ApiException("Serial Number already exists.", HttpStatus.CONFLICT);
        }

        AssetCategory category = assetCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset Category", request.getCategoryId()));

        Asset asset = new Asset();
        applyRequest(asset, request);
        asset.setCategory(category);
        asset.setStatus(AssetStatus.AVAILABLE);
        asset.setAssetTag(generateAssetTag());
        asset.setQrCode(asset.getAssetTag());

        return mapToResponse(assetRepository.save(asset));
    }

    @Override
    public AssetResponse updateAsset(Long assetId, AssetRequest request) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", assetId));

        if (!asset.getSerialNumber().equals(request.getSerialNumber())
                && assetRepository.existsBySerialNumber(request.getSerialNumber())) {
            throw new ApiException("Serial Number already exists.", HttpStatus.CONFLICT);
        }

        AssetCategory category = assetCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset Category", request.getCategoryId()));

        applyRequest(asset, request);
        asset.setCategory(category);

        return mapToResponse(assetRepository.save(asset));
    }

    @Override
    public void deleteAsset(Long assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", assetId));
        assetRepository.delete(asset);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetResponse getAssetById(Long assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", assetId));
        return mapToResponse(asset);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetResponse> getAllAssets(Pageable pageable) {
        return assetRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetResponse> searchAssets(String keyword, Pageable pageable) {
        return assetRepository
                .findByAssetNameContainingIgnoreCaseOrAssetTagContainingIgnoreCaseOrSerialNumberContainingIgnoreCase(
                        keyword, keyword, keyword, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssetResponse> filterAssets(String keyword, Long departmentId, Long categoryId,
                                             AssetStatus status, Pageable pageable) {
        // departmentId is accepted for API-compatibility with the directory screen's filter
        // bar, but has no effect until Allocation (Phase 3) links assets to departments.
        return assetRepository.findAll(
                AssetSpecification.search(keyword, categoryId, status),
                pageable
        ).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetResponse getAssetByTag(String assetTag) {
        Asset asset = assetRepository.findByAssetTag(assetTag)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", assetTag));
        return mapToResponse(asset);
    }

    @Override
    public AssetResponse changeAssetStatus(Long assetId, AssetStatus status) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset", assetId));

        lifecyclePolicy.validateTransition(asset.getStatus(), status);
        asset.setStatus(status);

        return mapToResponse(assetRepository.save(asset));
    }

    private void applyRequest(Asset asset, AssetRequest request) {
        asset.setAssetName(request.getAssetName());
        asset.setSerialNumber(request.getSerialNumber());
        asset.setManufacturer(request.getManufacturer());
        asset.setModel(request.getModel());
        asset.setPurchaseDate(request.getPurchaseDate());
        asset.setPurchaseCost(request.getPurchaseCost());
        asset.setWarrantyExpiry(request.getWarrantyExpiry());
        asset.setCurrentLocation(request.getCurrentLocation());
        asset.setCondition(request.getCondition());
        asset.setIsBookable(request.getIsBookable());
        asset.setNotes(request.getNotes());
    }

    private String generateAssetTag() {
        long next = assetRepository.count() + 1;
        String candidate = String.format("%s%04d", TAG_PREFIX, next);
        while (assetRepository.existsByAssetTag(candidate)) {
            next++;
            candidate = String.format("%s%04d", TAG_PREFIX, next);
        }
        return candidate;
    }

    private AssetResponse mapToResponse(Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .assetTag(asset.getAssetTag())
                .assetName(asset.getAssetName())
                .serialNumber(asset.getSerialNumber())
                .manufacturer(asset.getManufacturer())
                .model(asset.getModel())
                .purchaseDate(asset.getPurchaseDate())
                .purchaseCost(asset.getPurchaseCost())
                .warrantyExpiry(asset.getWarrantyExpiry())
                .currentLocation(asset.getCurrentLocation())
                .condition(asset.getCondition())
                .isBookable(asset.getIsBookable())
                .notes(asset.getNotes())
                .status(asset.getStatus())
                .qrCode(asset.getQrCode())
                .imageUrl(asset.getImageUrl())
                .categoryId(asset.getCategory().getId())
                .categoryName(asset.getCategory().getName())
                .createdAt(asset.getCreatedAt())
                .updatedAt(asset.getUpdatedAt())
                .build();
    }
}