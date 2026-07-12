package com.assetflow.config;

import com.assetflow.entity.*;
import com.assetflow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final AssetCategoryRepository categoryRepository;
    private final AssetRepository assetRepository;
    private final AllocationRepository allocationRepository;
    private final ResourceBookingRepository bookingRepository;
    private final MaintenanceRequestRepository maintenanceRepository;
    private final AuditCycleRepository auditCycleRepository;
    private final AuditAssignmentRepository auditAssignmentRepository;
    private final AuditFindingRepository auditFindingRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Force clean seeding for demo purposes

        // Clean up database tables to avoid unique constraints
        bookingRepository.deleteAllInBatch();
        allocationRepository.deleteAllInBatch();
        maintenanceRepository.deleteAllInBatch();
        auditFindingRepository.deleteAllInBatch();
        auditAssignmentRepository.deleteAllInBatch();
        auditCycleRepository.deleteAllInBatch();
        assetRepository.deleteAllInBatch();
        
        departmentRepository.findAll().forEach(dept -> {
            dept.setHead(null);
            departmentRepository.saveAndFlush(dept);
        });
        employeeRepository.deleteAllInBatch();
        departmentRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();

        // 1. Seed Categories
        AssetCategory electronics = categoryRepository.save(AssetCategory.builder()
                .name("Electronics")
                .description("Computers, phones, printers, and accessories")
                .status(EntityStatus.ACTIVE)
                .build());

        AssetCategory furniture = categoryRepository.save(AssetCategory.builder()
                .name("Furniture")
                .description("Desks, chairs, whiteboards, and conference tables")
                .status(EntityStatus.ACTIVE)
                .build());

        AssetCategory vehicles = categoryRepository.save(AssetCategory.builder()
                .name("Vehicles")
                .description("Company cars and delivery vans")
                .status(EntityStatus.ACTIVE)
                .build());

        // 2. Seed Departments (without heads first)
        Department engineering = departmentRepository.save(Department.builder()
                .name("Engineering")
                .status(EntityStatus.ACTIVE)
                .build());

        Department operations = departmentRepository.save(Department.builder()
                .name("Operations")
                .status(EntityStatus.ACTIVE)
                .build());

        Department qa = departmentRepository.save(Department.builder()
                .name("Quality Assurance")
                .parentDepartment(engineering)
                .status(EntityStatus.ACTIVE)
                .build());

        // 3. Seed Employees
        String commonPassword = passwordEncoder.encode("Password123");

        Employee admin = employeeRepository.save(Employee.builder()
                .fullName("Admin User")
                .email("admin@assetflow.com")
                .passwordHash(commonPassword)
                .role(Role.ADMIN)
                .status(EntityStatus.ACTIVE)
                .build());

        // Custom seeded admin for testing
        employeeRepository.save(Employee.builder()
                .fullName("Mukesh")
                .email("telimukesh2005@gmail.com")
                .passwordHash(commonPassword)
                .role(Role.ADMIN)
                .status(EntityStatus.ACTIVE)
                .build());

        Employee priya = employeeRepository.save(Employee.builder()
                .fullName("Priya Shah")
                .email("priya@assetflow.com")
                .passwordHash(commonPassword)
                .role(Role.DEPARTMENT_HEAD)
                .department(qa)
                .status(EntityStatus.ACTIVE)
                .build());

        Employee raj = employeeRepository.save(Employee.builder()
                .fullName("Raj Patel")
                .email("raj@assetflow.com")
                .passwordHash(commonPassword)
                .role(Role.ASSET_MANAGER)
                .department(operations)
                .status(EntityStatus.ACTIVE)
                .build());

        Employee vishal = employeeRepository.save(Employee.builder()
                .fullName("Vishal Mehta")
                .email("vishal@assetflow.com")
                .passwordHash(commonPassword)
                .role(Role.EMPLOYEE)
                .department(engineering)
                .status(EntityStatus.ACTIVE)
                .build());

        Employee vikram = employeeRepository.save(Employee.builder()
                .fullName("Vikram Singh")
                .email("vikram@assetflow.com")
                .passwordHash(commonPassword)
                .role(Role.EMPLOYEE)
                .department(engineering)
                .status(EntityStatus.ACTIVE)
                .build());

        // Update department heads
        engineering.setHead(priya);
        departmentRepository.save(engineering);

        qa.setHead(priya);
        departmentRepository.save(qa);

        operations.setHead(raj);
        departmentRepository.save(operations);

        // 4. Seed Assets
        Asset laptop = assetRepository.save(Asset.builder()
                .assetTag("AF-0114")
                .assetName("MacBook Pro 16")
                .serialNumber("AF-0114")
                .manufacturer("Apple")
                .model("M3 Pro")
                .purchaseDate(LocalDate.now().minusMonths(6))
                .purchaseCost(BigDecimal.valueOf(2500.00))
                .warrantyExpiry(LocalDate.now().plusYears(2))
                .currentLocation("Office 302")
                .condition(AssetCondition.GOOD)
                .isBookable(false)
                .notes("Assigned laptop for Priya Shah")
                .status(AssetStatus.ALLOCATED)
                .category(electronics)
                .build());

        Asset roomB2 = assetRepository.save(Asset.builder()
                .assetTag("AF-0002")
                .assetName("Conference Room B2")
                .serialNumber("CR-B2")
                .manufacturer("N/A")
                .model("8-Person Room")
                .purchaseDate(LocalDate.now().minusYears(2))
                .purchaseCost(BigDecimal.valueOf(5000.00))
                .warrantyExpiry(LocalDate.now().plusYears(1))
                .currentLocation("Floor 2")
                .condition(AssetCondition.NEW)
                .isBookable(true)
                .notes("Equipped with TV and whiteboards")
                .status(AssetStatus.AVAILABLE)
                .category(furniture)
                .build());

        Asset projector = assetRepository.save(Asset.builder()
                .assetTag("AF-0003")
                .assetName("Epson Projector P1")
                .serialNumber("PROJ-P1")
                .manufacturer("Epson")
                .model("PowerLite")
                .purchaseDate(LocalDate.now().minusMonths(12))
                .purchaseCost(BigDecimal.valueOf(600.00))
                .warrantyExpiry(LocalDate.now().plusMonths(6))
                .currentLocation("Floor 2 Cabinets")
                .condition(AssetCondition.GOOD)
                .isBookable(true)
                .notes("Available for temporary bookings")
                .status(AssetStatus.AVAILABLE)
                .category(electronics)
                .build());

        // 5. Seed Allocation (Priya Shah holds MacBook Pro AF-0114)
        allocationRepository.save(Allocation.builder()
                .asset(laptop)
                .employee(priya)
                .allocatedBy(raj)
                .expectedReturnDate(LocalDate.now().plusMonths(3))
                .status(AllocationStatus.ACTIVE)
                .build());

        // 6. Seed Booking (Priya books Room B2 for 9:00 - 10:00 today)
        LocalDateTime today9Am = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0));
        LocalDateTime today10Am = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0));

        bookingRepository.save(ResourceBooking.builder()
                .asset(roomB2)
                .bookedBy(priya)
                .startTime(today9Am)
                .endTime(today10Am)
                .purpose("Sprint Planning")
                .status(BookingStatus.UPCOMING)
                .build());

        // 7. Seed Maintenance Request (Projector P1 reported by Vikram)
        maintenanceRepository.save(MaintenanceRequest.builder()
                .asset(projector)
                .reportedBy(vikram)
                .description("Color balance is off, lamp needs replacement")
                .priority(MaintenancePriority.HIGH)
                .status(MaintenanceStatus.PENDING)
                .build());

        // 8. Seed Audit Cycle (Raj Patel auditors Engineering department)
        AuditCycle cycle = auditCycleRepository.save(AuditCycle.builder()
                .name("Q3 Hardware Audit")
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now().plusDays(5))
                .status(AuditCycleStatus.ACTIVE)
                .build());

        auditAssignmentRepository.save(AuditAssignment.builder()
                .auditCycle(cycle)
                .auditor(raj)
                .targetDepartment(qa)
                .build());

        auditFindingRepository.save(AuditFinding.builder()
                .auditCycle(cycle)
                .asset(laptop)
                .auditedBy(raj)
                .status(AuditFindingStatus.VERIFIED)
                .notes("In great condition, held by Priya Shah.")
                .build());
    }
}
