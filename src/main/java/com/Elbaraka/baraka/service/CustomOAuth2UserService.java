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
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(email, name));
        
        // User entity doesn't have lastLogin field, so we skip this
        // If needed, add lastLogin field to User entity later
        
        // Return OAuth2User with user authorities
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_CLIENT")),
                attributes,
                "email"
        );
    }
    
    private User createNewUser(String email, String fullName) {
        log.info("Creating new user from OAuth2: {}", email);
        
        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName != null && !fullName.trim().isEmpty() ? fullName : "OAuth2 User");
        user.setPassword(""); // OAuth2 users don't need password
        user.setActive(true);
        
        return userRepository.save(user);
    }
}
