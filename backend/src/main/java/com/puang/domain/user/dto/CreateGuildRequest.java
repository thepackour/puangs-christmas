package com.puang.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Body of POST /api/guilds.
 */
public record CreateGuildRequest(
        @NotBlank @Size(max = 30) String name,
        @Size(max = 255) String description
) {
}
