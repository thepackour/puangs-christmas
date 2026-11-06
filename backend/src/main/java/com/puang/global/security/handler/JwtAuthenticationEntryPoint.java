package com.puang.global.security.handler;

import com.puang.global.exception.ProjectException;
import com.puang.global.response.code.BaseErrorCode;
import com.puang.global.response.code.GeneralErrorCode;
import com.puang.global.security.filter.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Called when a protected URL is requested without a valid token (401).
 * Hands the error to GlobalExceptionHandler so the body uses the ApiResponse format.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver resolver;

    public JwtAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) {
        BaseErrorCode errorCode = GeneralErrorCode.UNAUTHORIZED;
        if (request.getAttribute(JwtAuthenticationFilter.ERROR_ATTRIBUTE) instanceof BaseErrorCode jwtError) {
            errorCode = jwtError;
        }
        resolver.resolveException(request, response, null, new ProjectException(errorCode));
    }
}
