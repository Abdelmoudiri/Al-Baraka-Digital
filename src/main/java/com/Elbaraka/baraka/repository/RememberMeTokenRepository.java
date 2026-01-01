package com.Elbaraka.baraka.repository;

import com.Elbaraka.baraka.entity.RememberMeToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository for managing persistent remember-me tokens.
 */
@Repository
public interface RememberMeTokenRepository extends JpaRepository<RememberMeToken, String> {
    
    /**
     * Find a token by series identifier.
     *
     * @param series the series identifier
     * @return optional containing the token if found
     */
    Optional<RememberMeToken> findBySeries(String series);
    
    /**
     * Delete all tokens for a specific username.
     *
     * @param username the username
     */
    void deleteByUsername(String username);
    
    /**
     * Delete tokens older than a specific date.
     *
     * @param date the cutoff date
     */
    void deleteByLastUsedBefore(LocalDateTime date);
}
