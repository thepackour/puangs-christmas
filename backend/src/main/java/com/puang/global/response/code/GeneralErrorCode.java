package com.puang.global.response.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GeneralErrorCode implements BaseErrorCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "Invalid request."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "Resource not found."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON405", "Method not allowed."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH401", "Authentication is required."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4011", "The token is invalid."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4012", "The token has expired."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH403", "You do not have permission."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "Internal server error.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
