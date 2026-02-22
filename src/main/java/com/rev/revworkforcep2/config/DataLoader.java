package com.rev.revworkforcep2.config;

import com.rev.revworkforcep2.model.*;
import com.rev.revworkforcep2.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner loadData() {
        return args -> {

            // =====================
            // CREATE OR GET DEPARTMENT
            // =====================
            Department hr = departmentRepository.findByName("HR")
                    .orElseGet(() -> {
                        Department dept = new Department();
                        dept.setName("HR");
                        dept.setDescription("Human Resources Department");
                        return departmentRepository.save(dept);
                    });

            // =====================
            // CREATE OR GET DESIGNATIONS
            // =====================
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

            // =====================
            // CREATE ADMIN
            // =====================
            if (!userRepository.existsByEmail("admin@gmail.com")) {

                User admin = new User();
                admin.setEmployeeId("EMP001");
                admin.setFirstName("System");
                admin.setLastName("Admin");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setActive(true);
                admin.setDepartment(hr);
                admin.setDesignation(managerDesignation);
                admin.setJoiningDate(LocalDate.now());

                userRepository.save(admin);
            }

            // =====================
            // CREATE MANAGER
            // =====================
            if (!userRepository.existsByEmail("manager@gmail.com")) {

                User manager = new User();
                manager.setEmployeeId("EMP003");
                manager.setFirstName("Team");
                manager.setLastName("Manager");
                manager.setEmail("manager@gmail.com");
                manager.setPassword(passwordEncoder.encode("admin123"));
                manager.setRole(Role.MANAGER);
                manager.setActive(true);
                manager.setDepartment(hr);
                manager.setDesignation(managerDesignation);
                manager.setJoiningDate(LocalDate.now());

                userRepository.save(manager);
            }

            // =====================
            // CREATE EMPLOYEE
            // =====================
            if (!userRepository.existsByEmail("employee@gmail.com")) {

                User employee = new User();
                employee.setEmployeeId("EMP002");
                employee.setFirstName("John");
                employee.setLastName("Doe");
                employee.setEmail("employee@gmail.com");
                employee.setPassword(passwordEncoder.encode("admin123"));
                employee.setRole(Role.EMPLOYEE);
                employee.setActive(true);
                employee.setDepartment(hr);
                employee.setDesignation(employeeDesignation);
                employee.setJoiningDate(LocalDate.now());

                userRepository.save(employee);
            }

            // =====================
            // ASSIGN MANAGER TO EMPLOYEE
            // =====================
            User managerUser = userRepository.findByEmail("manager@gmail.com")
                    .orElseThrow(() -> new RuntimeException("Manager not found"));

            User employeeUser = userRepository.findByEmail("employee@gmail.com")
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            if (employeeUser.getManager() == null) {
                employeeUser.setManager(managerUser);
                userRepository.save(employeeUser);
            }

            System.out.println("✅ Data verification completed successfully!");
        };
    }
}