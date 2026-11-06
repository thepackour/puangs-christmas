package com.puang.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateGuildRequest(
        @NotBlank @Size(max = 30) String name,
        @Size(max = 255) String description
) {
}
