package com.assetflow.controller;

import com.assetflow.dto.request.AssetRequest;
import com.assetflow.dto.response.AssetResponse;
import com.assetflow.entity.AssetStatus;
import com.assetflow.service.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public ResponseEntity<AssetResponse> createAsset(
            @Valid @RequestBody AssetRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assetService.createAsset(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public ResponseEntity<AssetResponse> updateAsset(
            @PathVariable Long id,
            @Valid @RequestBody AssetRequest request
    ) {
        return ResponseEntity.ok(assetService.updateAsset(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public ResponseEntity<Void> deleteAsset(
            @PathVariable Long id
    ) {
        assetService.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER','DEPARTMENT_HEAD','EMPLOYEE')")
    public ResponseEntity<Page<AssetResponse>> searchAssets(

            @RequestParam String keyword,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size

    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(

                assetService.searchAssets(
                        keyword,
                        pageable
                )

        );

    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER','DEPARTMENT_HEAD','EMPLOYEE')")
    public ResponseEntity<AssetResponse> getAsset(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(assetService.getAssetById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER','DEPARTMENT_HEAD','EMPLOYEE')")
    public ResponseEntity<Page<AssetResponse>> getAllAssets(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size

    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                assetService.getAllAssets(pageable)
        );
    }
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER','DEPARTMENT_HEAD','EMPLOYEE')")
    public ResponseEntity<Page<AssetResponse>> filterAssets(

            @RequestParam(required = false) String keyword,

            @RequestParam(required = false) Long departmentId,

            @RequestParam(required = false) Long categoryId,

            @RequestParam(required = false) AssetStatus status,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size

    ) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(

                assetService.filterAssets(

                        keyword,

                        departmentId,

                        categoryId,

                        status,

                        pageable

                )

        );

    }
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public ResponseEntity<AssetResponse> changeStatus(

            @PathVariable Long id,

            @RequestParam AssetStatus status

    ) {

        return ResponseEntity.ok(
                assetService.changeAssetStatus(id, status)
        );

    }

}