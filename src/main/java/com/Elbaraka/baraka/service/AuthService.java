package com.Elbaraka.baraka.service;

import com.Elbaraka.baraka.dto.AuthResponse;
import com.Elbaraka.baraka.dto.LoginRequest;
import com.Elbaraka.baraka.dto.RegisterRequest;
import com.Elbaraka.baraka.entity.Account;
import com.Elbaraka.baraka.entity.Role;
import com.Elbaraka.baraka.entity.User;
import com.Elbaraka.baraka.repository.AccountRepository;
import com.Elbaraka.baraka.repository.RoleRepository;
import com.Elbaraka.baraka.repository.UserRepository;
import com.Elbaraka.baraka.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, 
                       AccountRepository accountRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, 
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        // Récupérer le rôle CLIENT par défaut
        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("Rôle CLIENT non trouvé"));

        // Créer l'utilisateur
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        
        Set<Role> roles = new HashSet<>();
        roles.add(clientRole);
        user.setRoles(roles);
        user.setActive(true);
        
        User savedUser = userRepository.save(user);

        // Créer le compte bancaire automatiquement
        Account account = new Account();
        account.setAccountNumber("ACC-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());
        account.setBalance(BigDecimal.ZERO);
        account.setOwner(savedUser);
        accountRepository.save(account);

        // Générer le token JWT
        String token = jwtUtil.generateToken(savedUser.getEmail());

        String userRoles = savedUser.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(", "));

        return new AuthResponse(token, savedUser.getEmail(), savedUser.getFullName(), userRoles);
    }

    public AuthResponse login(LoginRequest request) {
        // Authentifier l'utilisateur
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Récupérer l'utilisateur
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier si le compte est actif
        if (!user.getActive()) {
            throw new RuntimeException("Compte désactivé");
        }

        // Générer le token JWT
        String token = jwtUtil.generateToken(user.getEmail());

        String userRoles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(", "));

        return new AuthResponse(token, user.getEmail(), user.getFullName(), userRoles);
    }
}
