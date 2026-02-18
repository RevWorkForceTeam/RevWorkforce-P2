package com.rev.revworkforcep2.service.auth;

import com.rev.revworkforcep2.dto.request.auth.LoginRequest;
import com.rev.revworkforcep2.dto.response.auth.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
