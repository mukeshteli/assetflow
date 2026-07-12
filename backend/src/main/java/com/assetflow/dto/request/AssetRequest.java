package com.assetflow.dto.request;

import com.assetflow.entity.AssetCondition;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetRequest {

    // Note: no assetTag field here — it's system-generated on create,
    // never accepted from the client.

    @NotBlank(message = "Asset name is required")
    @Size(max = 150, message = "Asset name cannot exceed 150 characters")
    private String assetName;

    @NotBlank(message = "Serial number is required")
    @Size(max = 100, message = "Serial number cannot exceed 100 characters")
    private String serialNumber;

    @Size(max = 100, message = "Manufacturer cannot exceed 100 characters")
    private String manufacturer;

    @Size(max = 100, message = "Model cannot exceed 100 characters")
    private String model;

    @PastOrPresent(message = "Purchase date cannot be in the future")
    private LocalDate purchaseDate;

    @PositiveOrZero(message = "Purchase cost must be positive")
    private BigDecimal purchaseCost;

    private LocalDate warrantyExpiry;

    @Size(max = 150, message = "Current location cannot exceed 150 characters")
    private String currentLocation;

    @NotNull(message = "Condition is required")
    private AssetCondition condition;

    @NotNull(message = "isBookable must be specified")
    private Boolean isBookable;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    @NotNull(message = "Asset category is required")
    private Long categoryId;

}