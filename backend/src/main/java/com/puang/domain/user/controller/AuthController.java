package com.puang.domain.user.controller;

import com.puang.domain.user.dto.LoginRequest;
import com.puang.domain.user.dto.RefreshRequest;
import com.puang.domain.user.dto.TokenResponse;
import com.puang.domain.user.service.AuthService;
import com.puang.global.response.ApiResponse;
import com.puang.global.response.code.GeneralSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, authService.login(request.email(), request.password()));
    }

    /**
     * Issues a new token pair when the access token has expired.
     */
    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, authService.refresh(request.refreshToken()));
    }
}
