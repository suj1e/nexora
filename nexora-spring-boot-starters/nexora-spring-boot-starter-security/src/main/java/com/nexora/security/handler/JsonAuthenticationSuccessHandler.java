package com.nexora.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.common.api.Result;
import com.nexora.common.model.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.Instant;

/**
 * JSON format authentication success handler.
 *
 * <p>Returns unified {@link Result} format with login response data.
 *
 * <p>Example response:
 * <pre>
 * {
 *   "code": 200,
 *   "success": true,
 *   "message": "success",
 *   "data": {
 *     "token": "xxx",
 *     "refreshToken": "yyy",
 *     "tokenType": "Bearer",
 *     "user": { ... }
 *   },
 *   "timestamp": "2024-01-28T10:00:00Z",
 *   "traceId": "xxx"
 * }
 * </pre>
 *
 * @author sujie
 * @since 1.0.0
 */
@Slf4j
public class JsonAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final AuthenticationSuccessHandler delegate;

    /**
     * Create handler with ObjectMapper and delegate for token generation.
     *
     * @param objectMapper the ObjectMapper for JSON serialization
     * @param delegate the delegate handler that generates tokens
     */
    public JsonAuthenticationSuccessHandler(
        ObjectMapper objectMapper,
        AuthenticationSuccessHandler delegate
    ) {
        this.objectMapper = objectMapper;
        this.delegate = delegate;
    }

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, jakarta.servlet.ServletException {
        log.debug("Authentication successful for user: {}", authentication.getName());

        // First call delegate to generate tokens and set them in request attributes
        delegate.onAuthenticationSuccess(request, response, authentication);

        // Then build unified Result response
        String token = (String) request.getAttribute("token");
        String refreshToken = (String) request.getAttribute("refreshToken");
        Instant expiresAt = (Instant) request.getAttribute("expiresAt");
        Instant refreshExpiresAt = (Instant) request.getAttribute("refreshExpiresAt");
        Object userInfo = request.getAttribute("user");

        LoginResponse.UserInfo user = userInfo != null
            ? (LoginResponse.UserInfo) userInfo
            : LoginResponse.UserInfo.builder()
                .id(authentication.getName())
                .username(authentication.getName())
                .build();

        LoginResponse loginResponse = LoginResponse.builder()
            .token(token)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresAt(expiresAt)
            .refreshExpiresAt(refreshExpiresAt)
            .user(user)
            .build();

        Result<LoginResponse> result = Result.ok(loginResponse);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
