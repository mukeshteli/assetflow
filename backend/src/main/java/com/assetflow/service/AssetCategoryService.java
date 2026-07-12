package com.assetflow.service;

import com.assetflow.dto.request.AssetCategoryRequest;
import com.assetflow.dto.response.AssetCategoryResponse;
import com.assetflow.entity.AssetCategory;
import com.assetflow.entity.Employee;
import com.assetflow.entity.EntityStatus;
import com.assetflow.exception.ResourceNotFoundException;
import com.assetflow.repository.AssetCategoryRepository;
import com.assetflow.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AssetCategoryService {

    private final AssetCategoryRepository assetCategoryRepository;
    private final EmployeeRepository employeeRepository;
    private final ActivityLogService activityLogService;

    @Transactional(readOnly = true)
    public List<AssetCategoryResponse> getAllCategories() {
        return assetCategoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public AssetCategoryResponse createCategory(AssetCategoryRequest request, String actorEmail) {
        Employee actor = employeeRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", actorEmail));

        AssetCategory category = AssetCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status(EntityStatus.ACTIVE)
                .build();

        category = assetCategoryRepository.save(category);

        activityLogService.log(actor, "CREATE_CATEGORY", "Created category " + category.getName());

        return toResponse(category);
    }

    public AssetCategoryResponse updateCategory(Long id, AssetCategoryRequest request, String actorEmail) {
        Employee actor = employeeRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", actorEmail));

        AssetCategory category = assetCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AssetCategory", id));

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        category = assetCategoryRepository.save(category);

        activityLogService.log(actor, "UPDATE_CATEGORY", "Updated category " + category.getName());

        return toResponse(category);
    }

    public AssetCategoryResponse changeCategoryStatus(Long id, EntityStatus status, String actorEmail) {
        Employee actor = employeeRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", actorEmail));

        AssetCategory category = assetCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AssetCategory", id));

        category.setStatus(status);
        category = assetCategoryRepository.save(category);

        activityLogService.log(actor, "CHANGE_CATEGORY_STATUS", "Changed status of category " + category.getName() + " to " + status.name());

        return toResponse(category);
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
