package com.Elbaraka.baraka.security;

import com.Elbaraka.baraka.entity.Permission;
import com.Elbaraka.baraka.entity.Role;
import com.Elbaraka.baraka.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class UserDetailsImpl implements UserDetails {

    private final String email;
    private final String password;
    private final boolean active;
    private final Set<GrantedAuthority> authorities;

    public UserDetailsImpl(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.active = user.getActive();
        this.authorities = getAuthoritiesFromUser(user);
    }

    /**
     * Extrait les autorités (rôles et permissions) de l'utilisateur
     */
    private Set<GrantedAuthority> getAuthoritiesFromUser(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                // Ajouter le rôle avec le préfixe ROLE_
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

                // Ajouter toutes les permissions du rôle
                if (role.getPermissions() != null) {
                    for (Permission permission : role.getPermissions()) {
                        authorities.add(new SimpleGrantedAuthority(permission.getName()));
                    }
                }
            }
        }

        return authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
