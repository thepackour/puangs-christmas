package com.puang.global.exception;

import com.puang.global.response.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProjectException extends RuntimeException {

    private final BaseErrorCode errorCode;

}
