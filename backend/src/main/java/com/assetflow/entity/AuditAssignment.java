package com.assetflow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "audit_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_cycle_id", nullable = false)
    private AuditCycle auditCycle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auditor_id", nullable = false)
    private Employee auditor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_department_id")
    private Department targetDepartment;

    @Column(name = "target_location", length = 100)
    private String targetLocation;
}
