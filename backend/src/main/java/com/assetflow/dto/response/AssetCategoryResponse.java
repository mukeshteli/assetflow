package com.assetflow.dto.response;

import com.assetflow.entity.EntityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetCategoryResponse {

    private Long id;
    private String name;
    private String description;
    private EntityStatus status;
}
