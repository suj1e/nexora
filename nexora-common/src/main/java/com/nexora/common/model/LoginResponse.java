package com.nexora.common.model;

import lombok.Builder;

import java.time.Instant;
import java.util.Map;

/**
 * Login response containing authentication tokens and user information.
 *
 * <p>Example:
 * <pre>
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIs...",
 *   "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
 *   "tokenType": "Bearer",
 *   "expiresAt": "2024-01-28T11:00:00Z",
 *   "refreshExpiresAt": "2024-02-04T10:00:00Z",
 *   "user": {
 *     "id": "123",
 *     "username": "john",
 *     "email": "john@example.com",
 *     "roles": ["USER", "ADMIN"]
 *   }
 * }
 * </pre>
 *
 * @author sujie
 * @since 1.0.0
 */
@Builder
public record LoginResponse(
    String token,
    String refreshToken,
    String tokenType,
    Instant expiresAt,
    Instant refreshExpiresAt,
    UserInfo user
) {

    /**
     * User information in login response.
     *
     * @author sujie
     * @since 1.0.0
     */
    @Builder
    public record UserInfo(
        String id,
        String username,
        String email,
        String avatar,
        String nickname,
        java.util.Set<String> roles,
        Map<String, Object> attributes
    ) {
    }

    /**
     * Create a basic login response with required fields.
     */
    public static LoginResponse of(String token, String refreshToken, UserInfo user) {
        return LoginResponse.builder()
            .token(token)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .user(user)
            .build();
    }
}
