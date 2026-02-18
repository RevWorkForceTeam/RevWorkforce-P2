package com.rev.revworkforcep2.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String type;
    private String role;
    private String email;
    private String name;
}
