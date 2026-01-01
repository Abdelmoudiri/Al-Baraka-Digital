package com.Elbaraka.baraka.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing persistent remember-me tokens for users.
 * Implements Spring Security's PersistentTokenBasedRememberMeServices.
 */
@Entity
@Table(name = "remember_me_tokens", indexes = {
    @Index(name = "idx_series", columnList = "series"),
    @Index(name = "idx_username", columnList = "username")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RememberMeToken {
    
    @Id
    @Column(name = "series", length = 64)
    private String series;
    
    @Column(name = "username", nullable = false, length = 64)
    private String username;
    
    @Column(name = "token", nullable = false, length = 64)
    private String token;
    
    @Column(name = "last_used", nullable = false)
    private LocalDateTime lastUsed;
}
