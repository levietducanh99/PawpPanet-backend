package com.pawpplanet.backend.auth.controller;

import com.nimbusds.jose.JOSEException;
import com.pawpplanet.backend.auth.dto.*;
import com.pawpplanet.backend.auth.service.AuthService;
import com.pawpplanet.backend.common.dto.ApiResponse;
import com.pawpplanet.backend.user.dto.UserResponse;
import com.pawpplanet.backend.user.entity.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<UserEntity> register(@RequestBody @Valid RegisterRequest request) {

        ApiResponse<UserEntity> response = new ApiResponse<>();
                response.setResult(authService.register(request));

                return response;
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        ApiResponse<AuthResponse> response = new ApiResponse<>();

        response.setResult(
                authService.login(request)
        );
        return response;
    }


    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        ApiResponse<IntrospectResponse> response = new ApiResponse<>();

        response.setResult(
                authService.introspect(request)
        );
        return response;
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUserById(@PathVariable("userId") Long userId) {
        ApiResponse<UserResponse> response = new ApiResponse<>();

        response.setResult(
                authService.getUserById(userId)
        );
        return response;
    }
}
