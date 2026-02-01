package com.nexora.security.handler;

import com.nexora.common.model.LoginResponse;
import com.nexora.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.time.Instant;
import java.util.Date;

/**
 * JWT token generation handler.
 *
 * <p>Generates JWT tokens and stores them in request attributes for later use.
 *
 * <p>This handler should be used together with {@link JsonAuthenticationSuccessHandler}.
 *
 * @author sujie
 */
@Slf4j
public class TokenAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;

    /**
     * Create handler with JwtTokenProvider.
     *
     * @param tokenProvider the JWT token provider
     */
    public TokenAuthenticationSuccessHandler(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) {
        String userId = authentication.getName();
        String username = authentication.getName();

        // Generate JWT tokens
        String token = tokenProvider.generateToken(userId, java.util.Map.of("username", username));
        String refreshToken = tokenProvider.generateRefreshToken(userId);

        // Calculate expiration times
        Date tokenExpiry = tokenProvider.getExpiration(token);
        Date refreshExpiry = tokenProvider.getExpiration(refreshToken);

        // Store in request attributes for later use
        request.setAttribute("token", token);
        request.setAttribute("refreshToken", refreshToken);
        request.setAttribute("expiresAt", tokenExpiry.toInstant());
        request.setAttribute("refreshExpiresAt", refreshExpiry.toInstant());

        // Build user info
        LoginResponse.UserInfo userInfo = buildUserInfo(authentication);

        request.setAttribute("user", userInfo);

        log.debug("Generated tokens for user: {}", userId);
    }

    /**
     * Build user info from authentication.
     *
     * @param authentication the authentication object
     * @return the user info
     */
    private LoginResponse.UserInfo buildUserInfo(Authentication authentication) {
        return LoginResponse.UserInfo.builder()
            .id(authentication.getName())
            .username(authentication.getName())
            .roles(authentication.getAuthorities().stream()
                .map(Object::toString)
                .collect(java.util.stream.Collectors.toSet()))
            .build();
    }
}
