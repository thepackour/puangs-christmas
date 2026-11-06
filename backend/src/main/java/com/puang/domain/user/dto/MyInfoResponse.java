package com.puang.domain.user.dto;

import com.puang.domain.user.entity.User;

public record MyInfoResponse(
        Long userId,
        String nickname,
        boolean allowNotification
) {
    public static MyInfoResponse from(User user) {
        return new MyInfoResponse(user.getId(), user.getNickname(), user.isAllowNotification());
    }
}
