package com.assetflow.controller;

import com.assetflow.dto.request.AssetCategoryRequest;
import com.assetflow.dto.response.AssetCategoryResponse;
import com.assetflow.entity.EntityStatus;
import com.assetflow.service.AssetCategoryService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public AssetCategoryResponse createCategory(
            @Valid @RequestBody AssetCategoryRequest request,
            Principal principal
    ) {
        return assetCategoryService.createCategory(request, principal.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AssetCategoryResponse updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody AssetCategoryRequest request,
            Principal principal
    ) {
        return assetCategoryService.updateCategory(id, request, principal.getName());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public AssetCategoryResponse changeStatus(
            @PathVariable Long id,
            @RequestParam EntityStatus status,
            Principal principal
    ) {
        return assetCategoryService.changeCategoryStatus(id, status, principal.getName());
    }
}