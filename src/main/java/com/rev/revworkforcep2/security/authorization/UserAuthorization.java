package com.rev.revworkforcep2.security.authorization;

import com.rev.revworkforcep2.model.User;
import com.rev.revworkforcep2.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserAuthorization {

    private final UserRepository userRepository;

    public UserAuthorization(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean canAccessUser(Long userId, String email) {

        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null) return false;

        // ADMIN can access anyone
        if (currentUser.getRole().name().equals("ADMIN")) {
            return true;
        }

        // EMPLOYEE can access only self
        if (currentUser.getRole().name().equals("EMPLOYEE")
                && currentUser.getId().equals(userId)) {
            return true;
        }

        // MANAGER can access direct reportees
        if (currentUser.getRole().name().equals("MANAGER")) {

            return userRepository
                    .findById(userId)
                    .map(user -> user.getManager() != null &&
                            user.getManager().getId().equals(currentUser.getId()))
                    .orElse(false);
        }

        return false;
    }
}
