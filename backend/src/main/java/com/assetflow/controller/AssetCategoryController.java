package com.assetflow.controller;

import com.assetflow.dto.response.AssetCategoryResponse;
import com.assetflow.service.AssetCategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class AssetCategoryController {

    private final AssetCategoryService assetCategoryService;

    public AssetCategoryController(AssetCategoryService assetCategoryService) {
        this.assetCategoryService = assetCategoryService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<AssetCategoryResponse> getAllCategories() {
        return assetCategoryService.getAllCategories();
    }
}