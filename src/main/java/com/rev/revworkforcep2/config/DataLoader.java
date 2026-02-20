package com.rev.revworkforcep2.config;

import com.rev.revworkforcep2.model.Department;
import com.rev.revworkforcep2.model.Designation;
import com.rev.revworkforcep2.model.Role;
import com.rev.revworkforcep2.model.User;
import com.rev.revworkforcep2.repository.DepartmentRepository;
import com.rev.revworkforcep2.repository.DesignationRepository;
import com.rev.revworkforcep2.repository.UserRepository;
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
            // CREATE ADMIN USER IF NOT EXISTS
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
                admin.setPhone("9999999999");
                admin.setAddress("Head Office");
                admin.setEmergencyContact("8888888888");
                admin.setSalary(100000.0);
                admin.setJoiningDate(LocalDate.now());

                userRepository.save(admin);
            }

            // =====================
            // CREATE EMPLOYEE USER IF NOT EXISTS
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
                employee.setPhone("7777777777");
                employee.setAddress("Chennai");
                employee.setEmergencyContact("6666666666");
                employee.setSalary(50000.0);
                employee.setJoiningDate(LocalDate.now());

                userRepository.save(employee);
            }

            System.out.println("✅ Data verification completed successfully!");
        };
    }
}