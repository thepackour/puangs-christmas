package com.puang.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Body of POST /api/auth/refresh.
 */
public record RefreshRequest(
        @NotBlank String refreshToken
) {
}
