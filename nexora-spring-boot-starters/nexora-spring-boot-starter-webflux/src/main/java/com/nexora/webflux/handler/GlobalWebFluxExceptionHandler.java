package com.nexora.webflux.handler;

import com.nexora.common.api.Result;
import com.nexora.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for WebFlux applications.
 *
 * <p>Handles exceptions and returns consistent error responses.
 *
 * @author sujie
 */
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class GlobalWebFluxExceptionHandler implements WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalWebFluxExceptionHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status;
        String message;
        int code;
        Map<String, Object> errors = null;

        if (ex instanceof BusinessException be) {
            status = HttpStatus.BAD_REQUEST;
            code = be.getCode();
            message = be.getMessage();
            log.warn("Business exception: {} - {}", code, message);
        } else if (ex instanceof WebExchangeBindException wbe) {
            status = HttpStatus.BAD_REQUEST;
            code = Result.CODE_VALIDATION_ERROR;
            message = "Validation failed";
            errors = new HashMap<>(wbe.getBindingResult().getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            e -> e.getDefaultMessage() != null ? e.getDefaultMessage() : "Invalid value",
                            (e1, e2) -> e1 + "; " + e2
                    )));
            log.warn("Validation error: {}", errors);
        } else if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
            code = Result.CODE_BAD_REQUEST;
            message = ex.getMessage();
            log.warn("Illegal argument: {}", message);
        } else if (ex instanceof IllegalStateException) {
            status = HttpStatus.BAD_REQUEST;
            code = Result.CODE_BAD_REQUEST;
            message = ex.getMessage();
            log.warn("Illegal state: {}", message);
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            code = Result.CODE_INTERNAL_ERROR;
            message = "Internal server error";
            log.error("Unhandled exception", ex);
        }

        return writeResponse(exchange, status, code, message, errors);
    }

    private Mono<Void> writeResponse(ServerWebExchange exchange, HttpStatus status,
                                      int code, String message, Map<String, Object> errors) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Result<?> result = Result.fail(code, message);

        if (errors != null && !errors.isEmpty()) {
            Map<String, Object> data = new HashMap<>();
            data.put("errors", errors);
            result = Result.fail(code, message, data);
        }

        String body = toJson(result);
        byte[] bytes = body.getBytes();

        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(bytes)));
    }

    private String toJson(Result<?> result) {
        // Simple JSON serialization for performance
        return String.format("""
                {"success":%s,"code":%d,"message":"%s","data":%s,"timestamp":%d}""",
                result.success(),
                result.code(),
                escapeJson(result.message()),
                result.data() != null ? escapeJson(result.data().toString()) : "null",
                System.currentTimeMillis());
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
