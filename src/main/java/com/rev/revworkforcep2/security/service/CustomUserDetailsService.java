package com.rev.revworkforcep2.security.service;

import com.rev.revworkforcep2.model.User;
import com.rev.revworkforcep2.repository.UserRepository;
import com.rev.revworkforcep2.security.model.CustomUserDetails;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrEmployeeId) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(emailOrEmployeeId)
                .or(() -> userRepository.findByEmployeeId(emailOrEmployeeId))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email or employee ID: " + emailOrEmployeeId));

        return new CustomUserDetails(user);
    }
}
