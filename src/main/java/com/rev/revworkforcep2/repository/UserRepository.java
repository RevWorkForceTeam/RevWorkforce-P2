package com.rev.revworkforcep2.repository;

import com.rev.revworkforcep2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends
        JpaRepository<User, Long>,
        JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmployeeId(String employeeId);

    List<User> findByDepartmentIdAndActiveTrue(Long departmentId);

    List<User> findByManagerIdAndActiveTrue(Long managerId);

    // change made by chaithanya
    List<User> findByManagerId(Long managerId);
}
