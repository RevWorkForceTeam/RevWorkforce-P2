package com.rev.revworkforcep2.controller.user;

import com.rev.revworkforcep2.dto.response.user.UserResponse;
import com.rev.revworkforcep2.util.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    /*Admin Only Example
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ApiResponse<UserResponse> createUser(...) {

    // Manager Only Example
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/team")
    public ApiResponse<List<UserResponse>> getTeamMembers() {

    // Employee Access (Self + higher roles)
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    @GetMapping("/profile")
    public ApiResponse<UserResponse> getProfile() {}
    */


}
