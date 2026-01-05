package com.Elbaraka.baraka.config;

import com.Elbaraka.baraka.entity.Account;
import com.Elbaraka.baraka.entity.Permission;
import com.Elbaraka.baraka.entity.Role;
import com.Elbaraka.baraka.entity.User;
import com.Elbaraka.baraka.repository.AccountRepository;
import com.Elbaraka.baraka.repository.PermissionRepository;
import com.Elbaraka.baraka.repository.RoleRepository;
import com.Elbaraka.baraka.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Initialise les données par défaut au démarrage de l'application.
 * Crée les permissions, rôles et utilisateurs de base si ils n'existent pas.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("=== Initialisation des données ===");
        
        initializePermissions();
        initializeRoles();
        initializeDefaultUsers();
        
        log.info("=== Initialisation des données terminée ===");
    }

    /**
     * Crée les permissions de base si elles n'existent pas.
     */
    private void initializePermissions() {
        log.info("Vérification des permissions...");
        
        createPermissionIfNotExists("USER_READ", "Lire les informations utilisateur");
        createPermissionIfNotExists("USER_WRITE", "Modifier les informations utilisateur");
        createPermissionIfNotExists("USER_DELETE", "Supprimer un utilisateur");
        
        createPermissionIfNotExists("ACCOUNT_READ", "Lire les informations de compte");
        createPermissionIfNotExists("ACCOUNT_WRITE", "Modifier un compte");
        createPermissionIfNotExists("ACCOUNT_CREATE", "Créer un compte");
        
        createPermissionIfNotExists("OPERATION_READ", "Voir les opérations");
        createPermissionIfNotExists("OPERATION_CREATE", "Créer une opération");
        createPermissionIfNotExists("OPERATION_APPROVE", "Approuver une opération");
        createPermissionIfNotExists("OPERATION_REJECT", "Rejeter une opération");
        
        createPermissionIfNotExists("DOCUMENT_READ", "Voir les documents");
        createPermissionIfNotExists("DOCUMENT_UPLOAD", "Télécharger des documents");
        createPermissionIfNotExists("DOCUMENT_DELETE", "Supprimer des documents");
        
        createPermissionIfNotExists("ADMIN_ACCESS", "Accès administration");
        createPermissionIfNotExists("AGENT_ACCESS", "Accès agent");
        
        log.info("Permissions initialisées.");
    }

    /**
     * Crée les rôles de base avec leurs permissions.
     */
    private void initializeRoles() {
        log.info("Vérification des rôles...");
        
        // Rôle ADMIN - toutes les permissions
        Set<Permission> adminPermissions = new HashSet<>(permissionRepository.findAll());
        createRoleIfNotExists("ROLE_ADMIN", "Administrateur avec tous les droits", adminPermissions);
        
        // Rôle AGENT - permissions agent
        Set<Permission> agentPermissions = new HashSet<>();
        addPermissionToSet(agentPermissions, "USER_READ");
        addPermissionToSet(agentPermissions, "ACCOUNT_READ");
        addPermissionToSet(agentPermissions, "ACCOUNT_WRITE");
        addPermissionToSet(agentPermissions, "OPERATION_READ");
        addPermissionToSet(agentPermissions, "OPERATION_APPROVE");
        addPermissionToSet(agentPermissions, "OPERATION_REJECT");
        addPermissionToSet(agentPermissions, "DOCUMENT_READ");
        addPermissionToSet(agentPermissions, "AGENT_ACCESS");
        createRoleIfNotExists("ROLE_AGENT", "Agent bancaire", agentPermissions);
        
        // Rôle CLIENT - permissions client
        Set<Permission> clientPermissions = new HashSet<>();
        addPermissionToSet(clientPermissions, "USER_READ");
        addPermissionToSet(clientPermissions, "ACCOUNT_READ");
        addPermissionToSet(clientPermissions, "OPERATION_READ");
        addPermissionToSet(clientPermissions, "OPERATION_CREATE");
        addPermissionToSet(clientPermissions, "DOCUMENT_READ");
        addPermissionToSet(clientPermissions, "DOCUMENT_UPLOAD");
        createRoleIfNotExists("ROLE_CLIENT", "Client de la banque", clientPermissions);
        
        log.info("Rôles initialisés.");
    }

    /**
     * Crée les utilisateurs par défaut (admin, agent, client de test).
     */
    private void initializeDefaultUsers() {
        log.info("Vérification des utilisateurs par défaut...");
        
        // Admin par défaut
        createUserIfNotExists(
            "admin@albaraka.com",
            "admin123",
            "Administrateur Système",
            "ROLE_ADMIN",
            new BigDecimal("0.00")
        );
        
        // Agent par défaut
        createUserIfNotExists(
            "agent@albaraka.com",
            "agent123",
            "Agent Al-Baraka",
            "ROLE_AGENT",
            new BigDecimal("0.00")
        );
        
        // Client de test
        createUserIfNotExists(
            "client@albaraka.com",
            "client123",
            "Client Test",
            "ROLE_CLIENT",
            new BigDecimal("10000.00")
        );
        
        log.info("Utilisateurs par défaut initialisés.");
    }

    private void createPermissionIfNotExists(String name, String description) {
        if (!permissionRepository.existsByName(name)) {
            Permission permission = new Permission();
            permission.setName(name);
            permission.setDescription(description);
            permissionRepository.save(permission);
            log.debug("Permission créée: {}", name);
        }
    }

    private void createRoleIfNotExists(String name, String description, Set<Permission> permissions) {
        if (!roleRepository.existsByName(name)) {
            Role role = new Role();
            role.setName(name);
            role.setDescription(description);
            role.setPermissions(permissions);
            roleRepository.save(role);
            log.info("Rôle créé: {} avec {} permissions", name, permissions.size());
        } else {
            // Mettre à jour les permissions si le rôle existe déjà
            roleRepository.findByName(name).ifPresent(role -> {
                role.setPermissions(permissions);
                roleRepository.save(role);
                log.debug("Rôle mis à jour: {}", name);
            });
        }
    }

    private void addPermissionToSet(Set<Permission> permissions, String permissionName) {
        permissionRepository.findByName(permissionName).ifPresent(permissions::add);
    }

    private void createUserIfNotExists(String email, String password, String fullName, 
                                        String roleName, BigDecimal initialBalance) {
        if (!userRepository.existsByEmail(email)) {
            // Créer l'utilisateur
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setFullName(fullName);
            user.setActive(true);
            
            // Assigner le rôle
            Set<Role> roles = new HashSet<>();
            roleRepository.findByName(roleName).ifPresent(roles::add);
            user.setRoles(roles);
            
            user = userRepository.save(user);
            
            // Créer le compte bancaire associé
            Account account = new Account();
            account.setAccountNumber(generateAccountNumber());
            account.setBalance(initialBalance);
            account.setOwner(user);
            accountRepository.save(account);
            
            log.info("Utilisateur créé: {} avec rôle {} et solde {}", email, roleName, initialBalance);
        }
    }

    /**
     * Génère un numéro de compte unique au format AL-XXXX-XXXX-XXXX
     */
    private String generateAccountNumber() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return String.format("AL-%s-%s-%s", 
            uuid.substring(0, 4), 
            uuid.substring(4, 8), 
            uuid.substring(8, 12));
    }
}
