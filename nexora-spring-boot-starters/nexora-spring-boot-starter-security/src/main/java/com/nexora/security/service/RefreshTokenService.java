package com.nexora.security.service;

import com.nexora.security.domain.RefreshToken;
import com.nexora.security.jwt.JwtTokenProvider;
import com.nexora.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing refresh tokens.
 *
 * <p>Usage:
 * <pre>
 * &#64;Autowired
 * private RefreshTokenService refreshTokenService;
 *
 * // Create refresh token
 * RefreshToken refreshToken = refreshTokenService.createRefreshToken(userId);
 *
 * // Validate refresh token
 * if (refreshTokenService.validateRefreshToken(token)) {
 *     RefreshToken rt = refreshTokenService.findByToken(token).orElseThrow();
 *     String accessToken = jwtTokenProvider.generateToken(rt.getUserId().toString(), claims);
 * }
 * </pre>
 *
 * @author sujie
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Create a new refresh token for the user.
     *
     * @param userId the user ID
     * @return the created refresh token
     */
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        // Delete existing tokens for this user (optional - keeps only one active token)
        refreshTokenRepository.deleteByUserId(userId);

        String token = jwtTokenProvider.generateRefreshToken(userId.toString());
        Instant expiryDate = Instant.now().plus(jwtTokenProvider.getProperties().getRefreshExpiration());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUserId(userId);
        refreshToken.setExpiryDate(expiryDate);

        RefreshToken saved = refreshTokenRepository.save(refreshToken);
        log.debug("Created refresh token for user: {}", userId);
        return saved;
    }

    /**
     * Find refresh token by token value.
     *
     * @param token the token value
     * @return the refresh token if found
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Validate refresh token.
     *
     * @param token the token value
     * @return true if valid
     */
    public boolean validateRefreshToken(String token) {
        return findByToken(token)
                .map(RefreshToken::isValid)
                .orElse(false);
    }

    /**
     * Delete refresh token.
     *
     * @param token the token value
     */
    @Transactional
    public void deleteRefreshToken(String token) {
        findByToken(token).ifPresent(refreshTokenRepository::delete);
    }

    /**
     * Revoke refresh token.
     *
     * @param token the token value
     */
    @Transactional
    public void revokeRefreshToken(String token) {
        findByToken(token).ifPresent(rt -> {
            rt.revoke();
            refreshTokenRepository.save(rt);
            log.debug("Revoked refresh token for user: {}", rt.getUserId());
        });
    }

    /**
     * Revoke all refresh tokens for a user.
     *
     * @param userId the user ID
     */
    @Transactional
    public void revokeAllUserTokens(Long userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
        log.debug("Revoked all refresh tokens for user: {}", userId);
    }

    /**
     * Delete all expired refresh tokens.
     *
     * @return number of deleted tokens
     */
    @Transactional
    public int deleteExpiredTokens() {
        List<RefreshToken> expiredTokens = refreshTokenRepository.findAll().stream()
                .filter(RefreshToken::isExpired)
                .toList();

        refreshTokenRepository.deleteAll(expiredTokens);
        log.debug("Deleted {} expired refresh tokens", expiredTokens.size());
        return expiredTokens.size();
    }

    /**
     * Get all valid refresh tokens for a user.
     *
     * @param userId the user ID
     * @return list of valid refresh tokens
     */
    public List<RefreshToken> getValidTokensByUserId(Long userId) {
        return refreshTokenRepository.findByUserIdAndRevokedIsFalseAndExpiryDateAfter(userId, Instant.now());
    }
}
