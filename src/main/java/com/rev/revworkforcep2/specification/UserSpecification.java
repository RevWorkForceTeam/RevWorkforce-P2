package com.rev.revworkforcep2.specification;

import com.rev.revworkforcep2.model.Role;
import com.rev.revworkforcep2.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> filterUsers(
            Long departmentId,
            Long designationId,
            Boolean active,
            String role
    ) {
        return (root, query, cb) -> {

            var predicate = cb.conjunction();

            if (departmentId != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("department").get("id"), departmentId));
            }

            if (designationId != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("designation").get("id"), designationId));
            }

            if (active != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("active"), active));
            }

            if (role != null && !role.isBlank()) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("role"), Role.valueOf(role.toUpperCase())));
            }

            return predicate;
        };
    }
}
