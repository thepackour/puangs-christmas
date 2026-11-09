package com.puang.domain.item.exception;

import com.puang.global.response.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ItemErrorCode implements BaseErrorCode {

    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "ITEM404", "Item not found.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
