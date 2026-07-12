package com.assetflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "audit_findings",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_cycle_asset", columnNames = {"audit_cycle_id", "asset_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditFinding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_cycle_id", nullable = false)
    private AuditCycle auditCycle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audited_by_id", nullable = false)
    private Employee auditedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditFindingStatus status;

    @Column(length = 1000)
    private String notes;

    @Column(name = "audited_at", nullable = false)
    private LocalDateTime auditedAt;

    @PrePersist
    protected void onCreate() {
        this.auditedAt = LocalDateTime.now();
    }
}
