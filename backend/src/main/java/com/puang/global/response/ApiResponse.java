package com.puang.global.response;

import com.puang.global.response.code.BaseErrorCode;
import com.puang.global.response.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean isSuccess;
    private String code;
    private String message;
    private T result;
    private T errorDetail;

    public static <T> ApiResponse<T> onSuccess(BaseSuccessCode code, T result) {
        return new ApiResponse<>(true, code.getCode(), code.getMessage(), result, null);
    }

    public static <T> ApiResponse<T> onFailure(BaseErrorCode code, T errorDetail) {
        return new ApiResponse<>(false, code.getCode(), code.getMessage(), null, errorDetail);
    }

}