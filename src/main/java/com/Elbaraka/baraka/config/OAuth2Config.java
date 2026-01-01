package com.Elbaraka.baraka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

/**
 * OAuth2 configuration for Google authentication.
 * Provides client registration for OAuth2 login flow.
 */
@Configuration
public class OAuth2Config {
    
    /**
     * Configure Google OAuth2 client registration.
     * Requires environment variables:
     * - GOOGLE_CLIENT_ID
     * - GOOGLE_CLIENT_SECRET
     *
     * @return client registration repository
     */
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(googleClientRegistration());
    }
    
    private ClientRegistration googleClientRegistration() {
        String clientId = System.getenv("GOOGLE_CLIENT_ID");
        String clientSecret = System.getenv("GOOGLE_CLIENT_SECRET");
        
        if (clientId == null || clientSecret == null) {
            throw new IllegalStateException(
                "Google OAuth2 credentials not configured. " +
                "Set GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET environment variables."
            );
        }
        
        return ClientRegistration.withRegistrationId("google")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid", "profile", "email")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .clientName("Google")
                .build();
    }
}
