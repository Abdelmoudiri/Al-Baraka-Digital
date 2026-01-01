package com.Elbaraka.baraka.service;

import com.Elbaraka.baraka.entity.RememberMeToken;
import com.Elbaraka.baraka.repository.RememberMeTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Custom implementation of PersistentTokenRepository for remember-me functionality.
 * Stores tokens in database using JPA.
 */
@Service
@RequiredArgsConstructor
public class CustomPersistentTokenRepository implements PersistentTokenRepository {
    
    private final RememberMeTokenRepository tokenRepository;
    
    @Override
    @Transactional
    public void createNewToken(PersistentRememberMeToken token) {
        RememberMeToken rememberMeToken = RememberMeToken.builder()
                .series(token.getSeries())
                .username(token.getUsername())
                .token(token.getTokenValue())
                .lastUsed(toLocalDateTime(token.getDate()))
                .build();
        
        tokenRepository.save(rememberMeToken);
    }
    
    @Override
    @Transactional
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        tokenRepository.findBySeries(series).ifPresent(token -> {
            token.setToken(tokenValue);
            token.setLastUsed(toLocalDateTime(lastUsed));
            tokenRepository.save(token);
        });
    }
    
    @Override
    @Transactional(readOnly = true)
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        return tokenRepository.findBySeries(seriesId)
                .map(token -> new PersistentRememberMeToken(
                        token.getUsername(),
                        token.getSeries(),
                        token.getToken(),
                        toDate(token.getLastUsed())
                ))
                .orElse(null);
    }
    
    @Override
    @Transactional
    public void removeUserTokens(String username) {
        tokenRepository.deleteByUsername(username);
    }
    
    /**
     * Clean up expired tokens (older than 30 days).
     */
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        tokenRepository.deleteByLastUsedBefore(cutoffDate);
    }
    
    private LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
    
    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }
}
