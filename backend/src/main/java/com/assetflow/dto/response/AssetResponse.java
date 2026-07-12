package com.assetflow.dto.response;

import com.assetflow.entity.AssetCondition;
import com.assetflow.entity.AssetStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetResponse {

    private Long id;
    private String assetTag;
    private String assetName;
    private String serialNumber;
    private String manufacturer;
    private String model;
    private LocalDate purchaseDate;
    private BigDecimal purchaseCost;
    private LocalDate warrantyExpiry;
    private String currentLocation;
    private AssetCondition condition;
    private Boolean isBookable;
    private String notes;
    private AssetStatus status;
    private String qrCode;
    private String imageUrl;

    private Long categoryId;
    private String categoryName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}