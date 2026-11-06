package com.puang.global.response.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GeneralSuccessCode implements BaseSuccessCode {

    OK(HttpStatus.OK, "COMMON200", "Success."),
    CREATED(HttpStatus.CREATED, "COMMON201", "Created.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
