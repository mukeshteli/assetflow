package com.assetflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "assets",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_asset_tag", columnNames = "asset_tag"),
                @UniqueConstraint(name = "uk_serial_number", columnNames = "serial_number")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // System-generated (e.g. AF-0001); never accepted from the client.
    @Column(name = "asset_tag", nullable = false, length = 100)
    private String assetTag;

    @Column(name = "asset_name", nullable = false, length = 150)
    private String assetName;

    @Column(name = "serial_number", nullable = false, length = 100)
    private String serialNumber;

    @Column(length = 100)
    private String manufacturer;

    @Column(length = 100)
    private String model;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "purchase_cost", precision = 12, scale = 2)
    private BigDecimal purchaseCost;

    @Column(name = "warranty_expiry")
    private LocalDate warrantyExpiry;

    @Column(name = "current_location", length = 150)
    private String currentLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AssetCondition condition = AssetCondition.NEW;

    // Whether this asset can be booked as a shared resource (Resource Booking screen).
    @Column(name = "is_bookable", nullable = false)
    @Builder.Default
    private Boolean isBookable = false;

    @Column(length = 1000)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AssetStatus status = AssetStatus.AVAILABLE;

    @Column(name = "qr_code")
    private String qrCode;

    @Column(name = "image_url")
    private String imageUrl;

    // No department/employee reference here on purpose: who currently holds
    // this asset is tracked by the Allocation entity (Phase 3), which also
    // gives us full allocation history and the double-allocation conflict check.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_asset_category")
    )
    private AssetCategory category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}