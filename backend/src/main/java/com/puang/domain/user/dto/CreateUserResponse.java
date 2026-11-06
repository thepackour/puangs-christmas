package com.puang.domain.user.dto;

import com.puang.domain.user.entity.User;

/**
 * Response of POST /api/users.
 */
public record CreateUserResponse(
        Long userId,
        String email,
        String nickname
) {
    public static CreateUserResponse from(User user) {
        return new CreateUserResponse(user.getId(), user.getEmail(), user.getNickname());
    }
}
