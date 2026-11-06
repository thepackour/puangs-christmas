package com.puang.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Body of POST /api/auth/token.
 */
public record TokenRequest(
        @NotBlank String uuid
) {
}
