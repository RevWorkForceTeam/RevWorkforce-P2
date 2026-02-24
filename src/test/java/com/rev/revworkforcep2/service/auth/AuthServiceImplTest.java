package com.rev.revworkforcep2.service.auth;

import com.rev.revworkforcep2.dto.request.auth.LoginRequest;
import com.rev.revworkforcep2.dto.response.auth.LoginResponse;
import com.rev.revworkforcep2.model.Role;
import com.rev.revworkforcep2.model.User;
import com.rev.revworkforcep2.repository.UserRepository;
import com.rev.revworkforcep2.security.jwt.JwtTokenProvider;
import com.rev.revworkforcep2.security.model.CustomUserDetails;
import com.rev.revworkforcep2.service.auth.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl service;


    // SUCCESS CASE


    @Test
    void login_shouldAuthenticateAndReturnLoginResponse() {

        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.com");
        request.setPassword("password");

        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        User user = new User();
        user.setEmail("test@mail.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(Role.ADMIN); // Adjust if your enum differs

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(jwtTokenProvider.generateToken(userDetails))
                .thenReturn("mocked-jwt-token");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        // Act
        LoginResponse response = service.login(request);

        // Assert
        assertThat(response.getToken()).isEqualTo("mocked-jwt-token");
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getRole()).isEqualTo("ADMIN");
        assertThat(response.getEmail()).isEqualTo("test@mail.com");
        assertThat(response.getName()).isEqualTo("John Doe");

        verify(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(jwtTokenProvider).generateToken(userDetails);
        verify(userRepository).findByEmail("test@mail.com");
    }


    // USER NOT FOUND CASE


    @Test
    void login_shouldThrowException_whenUserNotFound() {

        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@mail.com");
        request.setPassword("password");

        CustomUserDetails userDetails = mock(CustomUserDetails.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(jwtTokenProvider.generateToken(userDetails))
                .thenReturn("token");

        when(userRepository.findByEmail("unknown@mail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");

        verify(userRepository).findByEmail("unknown@mail.com");
    }
}