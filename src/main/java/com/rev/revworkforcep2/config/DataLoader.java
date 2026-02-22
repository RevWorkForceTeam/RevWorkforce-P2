package com.rev.revworkforcep2.config;

import com.rev.revworkforcep2.model.*;
import com.rev.revworkforcep2.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final HolidayRepository holidayRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner loadData() {
        return args -> {

            // ===================== DEPARTMENT =====================
            Department hr = departmentRepository.findByName("HR")
                    .orElseGet(() -> {
                        Department dept = new Department();
                        dept.setName("HR");
                        dept.setDescription("Human Resources Department");
                        return departmentRepository.save(dept);
                    });

            // ===================== DESIGNATIONS =====================
            Designation managerDesignation = designationRepository
                    .findByTitle("Manager")
                    .orElseGet(() -> {
                        Designation d = new Designation();
                        d.setTitle("Manager");
                        d.setDescription("Manages team members");
                        return designationRepository.save(d);
                    });

            Designation employeeDesignation = designationRepository
                    .findByTitle("Software Engineer")
                    .orElseGet(() -> {
                        Designation d = new Designation();
                        d.setTitle("Software Engineer");
                        d.setDescription("Handles development tasks");
                        return designationRepository.save(d);
                    });

            // ===================== USERS =====================
            User admin = createUserIfNotExists(
                    "admin@gmail.com", "EMP001",
                    "System", "Admin",
                    Role.ADMIN, hr, managerDesignation
            );

            User manager = createUserIfNotExists(
                    "manager@gmail.com", "EMP003",
                    "Team", "Manager",
                    Role.MANAGER, hr, managerDesignation
            );

            User employee = createUserIfNotExists(
                    "employee@gmail.com", "EMP002",
                    "John", "Doe",
                    Role.EMPLOYEE, hr, employeeDesignation
            );

            // Assign Manager
            if (employee.getManager() == null) {
                employee.setManager(manager);
                userRepository.save(employee);
            }

            // ===================== LEAVE TYPES =====================
            LeaveType casual = createLeaveTypeIfNotExists("Casual Leave", 12);
            LeaveType sick = createLeaveTypeIfNotExists("Sick Leave", 10);
            LeaveType paid = createLeaveTypeIfNotExists("Paid Leave", 15);

            // ===================== LEAVE BALANCES =====================
            List<User> users = List.of(admin, manager, employee);
            List<LeaveType> leaveTypes = List.of(casual, sick, paid);

            for (User u : users) {

                if (leaveBalanceRepository.findByUserId(u.getId()).isEmpty()) {

                    for (LeaveType lt : leaveTypes) {

                        LeaveBalance balance = new LeaveBalance();
                        balance.setUser(u);
                        balance.setLeaveType(lt);
                        balance.setTotalDays(lt.getDefaultQuota());
                        balance.setUsedDays(0);
                        balance.setRemainingDays(lt.getDefaultQuota());

                        leaveBalanceRepository.save(balance);
                    }
                }
            }

            // ===================== HOLIDAYS =====================
            if (holidayRepository.count() == 0) {

                Holiday h1 = new Holiday();
                h1.setName("New Year");
                h1.setHolidayDate(LocalDate.of(2026, 1, 1));

                Holiday h2 = new Holiday();
                h2.setName("Republic Day");
                h2.setHolidayDate(LocalDate.of(2026, 1, 26));

                Holiday h3 = new Holiday();
                h3.setName("Independence Day");
                h3.setHolidayDate(LocalDate.of(2026, 8, 15));

                Holiday h4 = new Holiday();
                h4.setName("Gandhi Jayanti");
                h4.setHolidayDate(LocalDate.of(2026, 10, 2));

                Holiday h5 = new Holiday();
                h5.setName("Christmas");
                h5.setHolidayDate(LocalDate.of(2026, 12, 25));

                holidayRepository.saveAll(List.of(h1, h2, h3, h4, h5));
            }

            System.out.println("✅ Default data loaded successfully!");
        };
    }

    // ===================== HELPER METHODS =====================

    private User createUserIfNotExists(
            String email,
            String empId,
            String first,
            String last,
            Role role,
            Department dept,
            Designation desig
    ) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User u = new User();
                    u.setEmployeeId(empId);
                    u.setFirstName(first);
                    u.setLastName(last);
                    u.setEmail(email);
                    u.setPassword(passwordEncoder.encode("admin123"));
                    u.setRole(role);
                    u.setActive(true);
                    u.setDepartment(dept);
                    u.setDesignation(desig);
                    u.setJoiningDate(LocalDate.now());
                    return userRepository.save(u);
                });
    }

    private LeaveType createLeaveTypeIfNotExists(String name, int quota) {
        return leaveTypeRepository.findByName(name)
                .orElseGet(() -> {
                    LeaveType lt = new LeaveType();
                    lt.setName(name);
                    lt.setDefaultQuota(quota);
                    return leaveTypeRepository.save(lt);
                });
    }
}