package com.nexora.security.autoconfigure;

import com.nexora.common.api.Result;
import com.nexora.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Spring Security exception handler.
 *
 * <p>Handles Spring Security exceptions and returns unified {@link Result} format.
 * Only activated when Spring Security is on the classpath.
 *
 * @author sujie
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnClass(AccessDeniedException.class)
@ConditionalOnWebApplication
public class SecurityExceptionHandler {

    /**
     * Handle access denied exceptions (Spring Security).
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        String traceId = Result.generateTraceId();
        log.warn("Access denied: [{}] {} - {}", traceId, request.getRequestURI(), ex.getMessage());
        return Result.failWithTraceId(BusinessException.ErrorCode.FORBIDDEN.getCode(),
            BusinessException.ErrorCode.FORBIDDEN.getDefaultMessage(), traceId);
    }
}
