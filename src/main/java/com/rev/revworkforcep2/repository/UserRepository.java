package com.rev.revworkforcep2.repository;

import com.rev.revworkforcep2.model.User;
import com.rev.revworkforcep2.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmployeeId(String employeeId);

    boolean existsByEmail(String email);

    boolean existsByEmployeeId(String employeeId);

    List<User> findByManagerId(Long managerId);

    List<User> findByDepartmentId(Long departmentId);

    List<User> findByRole(Role role);
}
