package com.rev.revworkforcep2.controller.auth;

import com.rev.revworkforcep2.dto.request.auth.LoginRequest;
import com.rev.revworkforcep2.dto.response.auth.LoginResponse;
import com.rev.revworkforcep2.service.auth.AuthService;
import com.rev.revworkforcep2.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ApiResponse.success(
                200,
                "Login successful",
                response
        );
    }

}
