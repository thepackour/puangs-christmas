package com.puang.global.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.puang.global.response.code.BaseErrorCode;
import com.puang.global.response.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result", "errorDetail"})
public class ApiResponse<T> {

    // Lombok names the getter isSuccess(), which Jackson would turn into "success".
    // Putting @JsonProperty on the getter keeps the JSON key as "isSuccess".
    @Getter(onMethod_ = @JsonProperty("isSuccess"))
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