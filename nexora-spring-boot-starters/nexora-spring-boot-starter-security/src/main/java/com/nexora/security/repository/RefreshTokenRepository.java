package com.nexora.security.repository;

import com.nexora.security.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link RefreshToken} entities.
 *
 * @author sujie
 * @since 1.0.0
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Find refresh token by token value.
     *
     * @param token the token value
     * @return the refresh token if found
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Find all valid (non-expired, non-revoked) refresh tokens for a user.
     *
     * @param userId the user ID
     * @return list of valid refresh tokens
     */
    List<RefreshToken> findByUserIdAndRevokedIsFalseAndExpiryDateAfter(Long userId, Instant now);

    /**
     * Find all refresh tokens for a user.
     *
     * @param userId the user ID
     * @return list of all refresh tokens
     */
    List<RefreshToken> findByUserId(Long userId);

    /**
     * Delete all expired refresh tokens.
     *
     * @param now the current time
     * @return number of deleted tokens
     */
    @Transactional
    void deleteByExpiryDateBefore(Instant now);

    /**
     * Delete all refresh tokens for a user.
     *
     * @param userId the user ID
     */
    @Transactional
    void deleteByUserId(Long userId);

    /**
     * Revoke all refresh tokens for a user.
     *
     * @param userId the user ID
     */
    @Transactional
    default void revokeAllByUserId(Long userId) {
        List<RefreshToken> tokens = findByUserId(userId);
        tokens.forEach(RefreshToken::revoke);
        saveAll(tokens);
    }
}
