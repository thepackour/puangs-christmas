package com.puang.global.exception;

import com.puang.global.response.ApiResponse;
import com.puang.global.response.code.BaseErrorCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProjectException.class)
    public ApiResponse<Void> handleProjectException(
            ProjectException e
    ) {
        BaseErrorCode errorCode = e.getErrorCode();
        return ApiResponse.onFailure(
                errorCode, null
        );
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(
            ProjectException e
    ) {
        BaseErrorCode errorCode = e.getErrorCode();
        return ApiResponse.onFailure(
                errorCode, null
        );
    }

}
