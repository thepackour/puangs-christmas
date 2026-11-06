package com.puang.domain.user.exception;

import com.puang.global.response.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404", "User not found."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER409", "This email is already registered."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "USER401", "Email or password is incorrect.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
