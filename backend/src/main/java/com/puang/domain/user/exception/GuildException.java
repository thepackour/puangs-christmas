package com.puang.domain.user.exception;

import com.puang.global.exception.ProjectException;
import com.puang.global.response.code.BaseErrorCode;

/**
 * Thrown for every guild-related failure (not found, not the leader, duplicate name, ...).
 * Extends ProjectException, so GlobalExceptionHandler already turns it into an ApiResponse.
 */
public class GuildException extends ProjectException {

    public GuildException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
