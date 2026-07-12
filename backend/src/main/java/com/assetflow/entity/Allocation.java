package com.assetflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "allocations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Allocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allocated_by_id", nullable = false)
    private Employee allocatedBy;

    @Column(name = "allocated_at", nullable = false)
    private LocalDateTime allocatedAt;

    @Column(name = "expected_return_date")
    private LocalDate expectedReturnDate;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Column(name = "return_notes", length = 1000)
    private String returnNotes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AllocationStatus status = AllocationStatus.ACTIVE;

    @PrePersist
    protected void onCreate() {
        this.allocatedAt = LocalDateTime.now();
    }
}
