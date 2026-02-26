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
    private final LeaveApplicationRepository leaveApplicationRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner loadData() {
        return args -> {

            // Department
            Department hr = departmentRepository.findByName("HR")
                    .orElseGet(() -> {
                        Department dept = new Department();
                        dept.setName("HR");
                        dept.setDescription("Human Resources Department");
                        return departmentRepository.save(dept);
                    });

            Department engineering = departmentRepository.findByName("Engineering")
                    .orElseGet(() -> {
                        Department dept = new Department();
                        dept.setName("Engineering");
                        dept.setDescription("Software Development and Engineering");
                        return departmentRepository.save(dept);
                    });

            Department finance = departmentRepository.findByName("Finance")
                    .orElseGet(() -> {
                        Department dept = new Department();
                        dept.setName("Finance");
                        dept.setDescription("Financial Planning and Accounting");
                        return departmentRepository.save(dept);
                    });

            Department marketing = departmentRepository.findByName("Marketing")
                    .orElseGet(() -> {
                        Department dept = new Department();
                        dept.setName("Marketing");
                        dept.setDescription("Marketing and Sales");
                        return departmentRepository.save(dept);
                    });

            Department operations = departmentRepository.findByName("Operations")
                    .orElseGet(() -> {
                        Department dept = new Department();
                        dept.setName("Operations");
                        dept.setDescription("Business Operations");
                        return departmentRepository.save(dept);
                    });

            //  Designations
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

            designationRepository.findByTitle("Senior Developer")
                    .orElseGet(() -> {
                        Designation d = new Designation();
                        d.setTitle("Senior Developer");
                        d.setDescription("Senior software developer");
                        return designationRepository.save(d);
                    });

            designationRepository.findByTitle("HR Executive")
                    .orElseGet(() -> {
                        Designation d = new Designation();
                        d.setTitle("HR Executive");
                        d.setDescription("Human resources executive");
                        return designationRepository.save(d);
                    });

            designationRepository.findByTitle("Finance Analyst")
                    .orElseGet(() -> {
                        Designation d = new Designation();
                        d.setTitle("Finance Analyst");
                        d.setDescription("Financial analysis and reporting");
                        return designationRepository.save(d);
                    });

            designationRepository.findByTitle("Marketing Executive")
                    .orElseGet(() -> {
                        Designation d = new Designation();
                        d.setTitle("Marketing Executive");
                        d.setDescription("Marketing and promotions");
                        return designationRepository.save(d);
                    });

            designationRepository.findByTitle("Operations Manager")
                    .orElseGet(() -> {
                        Designation d = new Designation();
                        d.setTitle("Operations Manager");
                        d.setDescription("Manages business operations");
                        return designationRepository.save(d);
                    });

            // Users
            User admin = createUserIfNotExists(
                    "karthik@revworkforce.com", "EMP001",
                    "Karthik", "Nalla",
                    Role.ADMIN, hr, managerDesignation
            );

            User manager = createUserIfNotExists(
                    "chaithanya@revworkforce.com", "EMP002",
                    "Chaithanya", "Palamani",
                    Role.MANAGER, hr, managerDesignation
            );

            User employee = createUserIfNotExists(
                    "john@revworkforce.com", "EMP003",
                    "John", "Doe",
                    Role.EMPLOYEE, hr, employeeDesignation
            );

            // Assign Manager
            if (employee.getManager() == null) {
                employee.setManager(manager);
                userRepository.save(employee);
            }

            // Leave Types
            LeaveType casual = createLeaveTypeIfNotExists("Casual Leave", 12);
            LeaveType sick = createLeaveTypeIfNotExists("Sick Leave", 10);
            LeaveType paid = createLeaveTypeIfNotExists("Paid Leave", 15);

            // Leave balances
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

            // Holidays
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

            // Leave Application
            if (leaveApplicationRepository.count() == 0) {
                // Employee leaves
                LeaveApplication leave1 = new LeaveApplication();
                leave1.setUser(employee);
                leave1.setLeaveType(casual);
                leave1.setStartDate(LocalDate.now().minusDays(10));
                leave1.setEndDate(LocalDate.now().minusDays(8));
                leave1.setReason("Family function");
                leave1.setStatus(LeaveStatus.APPROVED);
                leave1.setManager(manager);
                leaveApplicationRepository.save(leave1);

                LeaveApplication leave2 = new LeaveApplication();
                leave2.setUser(employee);
                leave2.setLeaveType(sick);
                leave2.setStartDate(LocalDate.now().plusDays(5));
                leave2.setEndDate(LocalDate.now().plusDays(6));
                leave2.setReason("Medical checkup");
                leave2.setStatus(LeaveStatus.PENDING);
                leave2.setManager(manager);
                leaveApplicationRepository.save(leave2);

                // Manager leaves
                LeaveApplication leave3 = new LeaveApplication();
                leave3.setUser(manager);
                leave3.setLeaveType(paid);
                leave3.setStartDate(LocalDate.now().minusDays(5));
                leave3.setEndDate(LocalDate.now().minusDays(3));
                leave3.setReason("Vacation");
                leave3.setStatus(LeaveStatus.APPROVED);
                leaveApplicationRepository.save(leave3);

                LeaveApplication leave4 = new LeaveApplication();
                leave4.setUser(manager);
                leave4.setLeaveType(casual);
                leave4.setStartDate(LocalDate.now().plusDays(10));
                leave4.setEndDate(LocalDate.now().plusDays(11));
                leave4.setReason("Personal work");
                leave4.setStatus(LeaveStatus.PENDING);
                leaveApplicationRepository.save(leave4);

                // Admin leaves
                LeaveApplication leave5 = new LeaveApplication();
                leave5.setUser(admin);
                leave5.setLeaveType(sick);
                leave5.setStartDate(LocalDate.now().minusDays(20));
                leave5.setEndDate(LocalDate.now().minusDays(19));
                leave5.setReason("Fever");
                leave5.setStatus(LeaveStatus.APPROVED);
                leaveApplicationRepository.save(leave5);

                LeaveApplication leave6 = new LeaveApplication();
                leave6.setUser(admin);
                leave6.setLeaveType(casual);
                leave6.setStartDate(LocalDate.now().plusDays(3));
                leave6.setEndDate(LocalDate.now().plusDays(4));
                leave6.setReason("Conference");
                leave6.setStatus(LeaveStatus.REJECTED);
                leave6.setManagerComment("Not approved due to critical meeting");
                leaveApplicationRepository.save(leave6);
            }

            System.out.println("Default data loaded successfully!");
        };
    }



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