package com.Elbaraka.baraka.service;

import com.Elbaraka.baraka.entity.User;
import com.Elbaraka.baraka.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

/**
 * Custom OAuth2 user service that handles user creation/update from OAuth2 providers.
 * Creates new users or updates existing ones based on OAuth2 profile information.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        
        log.info("OAuth2 login attempt for email: {}", email);
        
        // Find or create user
        User user = userRepository.findByUsername(email)
                .orElseGet(() -> createNewUser(email, name));
        
        // Update last login
        user.setLastLogin(java.time.LocalDateTime.now());
        userRepository.save(user);
        
        // Return OAuth2User with user authorities
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_CLIENT")),
                attributes,
                "email"
        );
    }
    
    private User createNewUser(String email, String name) {
        log.info("Creating new user from OAuth2: {}", email);
        
        User user = new User();
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(extractFirstName(name));
        user.setLastName(extractLastName(name));
        user.setActive(true);
        user.setCreatedAt(java.time.LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "User";
        }
        String[] parts = fullName.trim().split("\\s+");
        return parts[0];
    }
    
    private String extractLastName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 1 ? parts[parts.length - 1] : "";
    }
}
