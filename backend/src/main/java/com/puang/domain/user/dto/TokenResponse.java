package com.puang.domain.user.dto;

/**
 * Response of POST /api/auth/login and POST /api/auth/refresh.
 * When the access token expires (401 AUTH4012), call POST /api/auth/refresh with the refresh token.
 */
public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn
) {
}
