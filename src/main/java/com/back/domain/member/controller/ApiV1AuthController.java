package com.back.domain.member.controller;

import com.back.domain.member.dto.LoginRequest;
import com.back.domain.member.dto.LoginResponse;
import com.back.domain.member.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class ApiV1AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return LoginResponse.bearer(authService.login(request.email(), request.password()));
    }
}