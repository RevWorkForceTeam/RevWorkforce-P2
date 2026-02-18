package com.rev.revworkforcep2.service.auth.impl;

import com.rev.revworkforcep2.dto.request.auth.LoginRequest;
import com.rev.revworkforcep2.dto.response.auth.LoginResponse;
import com.rev.revworkforcep2.model.User;
import com.rev.revworkforcep2.repository.UserRepository;
import com.rev.revworkforcep2.security.jwt.JwtTokenProvider;
import com.rev.revworkforcep2.security.model.CustomUserDetails;
import com.rev.revworkforcep2.service.auth.AuthService;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider,
                           UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        String token = jwtTokenProvider.generateToken(userDetails);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String fullName = user.getFirstName() + " " + user.getLastName();

        return new LoginResponse(
                token,
                "Bearer",
                user.getRole().name(),
                user.getEmail(),
                fullName
        );
    }
}
