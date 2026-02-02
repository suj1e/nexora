package com.nexora.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.common.api.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

/**
 * JSON format authentication failure handler.
 *
 * <p>Returns unified {@link Result} format with error details.
 *
 * <p>Example response:
 * <pre>
 * {
 *   "code": 401,
 *   "success": false,
 *   "message": "Authentication failed: Invalid credentials",
 *   "data": null,
 *   "timestamp": "2024-01-28T10:00:00Z",
 *   "traceId": "xxx"
 * }
 * </pre>
 *
 * @author sujie
 * @since 1.0.0
 */
@Slf4j
public class JsonAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    /**
     * Create handler with ObjectMapper.
     *
     * @param objectMapper the ObjectMapper for JSON serialization
     */
    public JsonAuthenticationFailureHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException exception
    ) throws IOException {
        log.warn("Authentication failed: {}", exception.getMessage());

        Result<Void> result = Result.failWithTraceId(
            HttpServletResponse.SC_UNAUTHORIZED,
            "Authentication failed: " + exception.getMessage(),
            Result.generateTraceId()
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
