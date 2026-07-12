package com.assetflow.service;

import com.assetflow.dto.response.AssetCategoryResponse;
import com.assetflow.entity.AssetCategory;
import com.assetflow.repository.AssetCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssetCategoryService {

    private final AssetCategoryRepository assetCategoryRepository;

    public AssetCategoryService(AssetCategoryRepository assetCategoryRepository) {
        this.assetCategoryRepository = assetCategoryRepository;
    }

    @Transactional(readOnly = true)
    public List<AssetCategoryResponse> getAllCategories() {
        return assetCategoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private AssetCategoryResponse toResponse(AssetCategory category) {
        return AssetCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .status(category.getStatus())
                .build();
    }
}
