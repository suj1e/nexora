package com.nexora.web.autoconfigure;

import com.nexora.common.api.Result;
import com.nexora.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * Global exception handler.
 *
 * <p>Automatically handles all exceptions and returns unified {@link Result} format.
 *
 * <p>Features:
 * <ul>
 *   <li>Automatic trace ID generation for error tracking</li>
 *   <li>Detailed error logging with request context</li>
 *   <li>Standardized error responses</li>
 * </ul>
 *
 * @author sujie
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle business exceptions.
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        String traceId = ex.getTraceId();
        log.warn("Business exception: [{}] {} - {} - context: {}",
            traceId, request.getRequestURI(), ex.getMessage(), ex.getContext());
        return Result.fail(ex);
    }

    /**
     * Handle validation exceptions (Bean Validation on request body).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String traceId = Result.generateTraceId();
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        log.warn("Validation exception: [{}] {} - {}", traceId, request.getRequestURI(), message);
        return Result.failWithTraceId(BusinessException.ErrorCode.VALIDATION_FAILED.getCode(), message, traceId);
    }

    /**
     * Handle constraint violation exceptions (Method Validation on parameters).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        String traceId = Result.generateTraceId();
        String message = ex.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));
        log.warn("Constraint violation: [{}] {} - {}", traceId, request.getRequestURI(), message);
        return Result.failWithTraceId(BusinessException.ErrorCode.VALIDATION_FAILED.getCode(), message, traceId);
    }

    /**
     * Handle illegal argument exceptions.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        String traceId = Result.generateTraceId();
        log.warn("Illegal argument: [{}] {} - {}", traceId, request.getRequestURI(), ex.getMessage());
        return Result.failWithTraceId(BusinessException.ErrorCode.BAD_REQUEST.getCode(), ex.getMessage(), traceId);
    }

    /**
     * Handle illegal state exceptions.
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalStateException(IllegalStateException ex, HttpServletRequest request) {
        String traceId = Result.generateTraceId();
        log.warn("Illegal state: [{}] {} - {}", traceId, request.getRequestURI(), ex.getMessage());
        return Result.failWithTraceId(BusinessException.ErrorCode.INVALID_STATE.getCode(), ex.getMessage(), traceId);
    }

    /**
     * Handle resource not found exceptions (JPA).
     */
    @ExceptionHandler({jakarta.persistence.EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleEntityNotFoundException(RuntimeException ex, HttpServletRequest request) {
        String traceId = Result.generateTraceId();
        log.warn("Entity not found: [{}] {} - {}", traceId, request.getRequestURI(), ex.getMessage());
        return Result.failWithTraceId(BusinessException.ErrorCode.RESOURCE_NOT_FOUND.getCode(),
            "Resource not found", traceId);
    }

    /**
     * Handle 404 errors (NoHandlerFoundException).
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        String traceId = Result.generateTraceId();
        log.warn("No handler found: [{}] {} - {}", traceId, request.getRequestURI(), ex.getRequestURL());
        return Result.failWithTraceId(BusinessException.ErrorCode.NOT_FOUND.getCode(),
            "Endpoint not found: " + ex.getRequestURL(), traceId);
    }

    /**
     * Handle HTTP method not supported.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String traceId = Result.generateTraceId();
        log.warn("Method not supported: [{}] {} - {}", traceId, request.getMethod(), ex.getMessage());
        return Result.failWithTraceId(Result.CODE_METHOD_NOT_ALLOWED,
            Result.MSG_METHOD_NOT_ALLOWED + ": " + ex.getMethod(), traceId);
    }

    /**
     * Handle media type not acceptable.
     */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public Result<Void> handleMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpServletRequest request) {
        String traceId = Result.generateTraceId();
        log.warn("Media type not acceptable: [{}] {} - {}", traceId, request.getRequestURI(), ex.getMessage());
        return Result.failWithTraceId(Result.CODE_NOT_ACCEPTABLE, Result.MSG_NOT_ACCEPTABLE, traceId);
    }

    /**
     * Handle message not readable (malformed JSON).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String traceId = Result.generateTraceId();
        log.warn("Message not readable: [{}] {} - {}", traceId, request.getRequestURI(), ex.getMessage());
        return Result.failWithTraceId(BusinessException.ErrorCode.BAD_REQUEST.getCode(),
            "Malformed request body", traceId);
    }

    /**
     * Handle missing request parameter.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMissingParameter(MissingServletRequestParameterException ex, HttpServletRequest request) {
        String traceId = Result.generateTraceId();
        log.warn("Missing parameter: [{}] {} - {}", traceId, request.getRequestURI(), ex.getParameterName());
        return Result.failWithTraceId(BusinessException.ErrorCode.BAD_REQUEST.getCode(),
            "Missing required parameter: " + ex.getParameterName(), traceId);
    }

    /**
     * Handle method argument type mismatch.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String traceId = Result.generateTraceId();
        log.warn("Type mismatch: [{}] {} - {} for parameter {}", traceId, request.getRequestURI(),
            ex.getValue(), ex.getName());
        return Result.failWithTraceId(BusinessException.ErrorCode.BAD_REQUEST.getCode(),
            "Invalid parameter type for: " + ex.getName(), traceId);
    }

    /**
     * Handle file upload size exceeded.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        String traceId = Result.generateTraceId();
        log.warn("Upload size exceeded: [{}] {} - {}", traceId, request.getRequestURI(), ex.getMessage());
        return Result.failWithTraceId(BusinessException.ErrorCode.BAD_REQUEST.getCode(),
            "File upload size exceeded", traceId);
    }

    /**
     * Handle all other exceptions.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception ex, HttpServletRequest request) {
        String traceId = Result.generateTraceId();
        log.error("Unexpected error: [{}] {} - {}", traceId, request.getRequestURI(), ex.getMessage(), ex);
        return Result.failWithTraceId(Result.CODE_INTERNAL_SERVER_ERROR, Result.MSG_INTERNAL_SERVER_ERROR, traceId);
    }
}
