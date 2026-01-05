# 📚 Al-Baraka Digital - Guide Complet du Projet

## 📋 Table des Matières
1. [Introduction](#1-introduction)
2. [Configuration du Projet (pom.xml)](#2-configuration-du-projet-pomxml)
3. [application.properties](#3-applicationproperties)
4. [Structure du Projet](#4-structure-du-projet)
5. [Les Entités (Entities)](#5-les-entités-entities)
6. [Les Enums](#6-les-enums)
7. [Les Repositories](#7-les-repositories)
8. [Les DTOs](#8-les-dtos)
9. [Les Services](#9-les-services)
10. [Les Utilitaires (JwtUtil)](#10-les-utilitaires-jwtutil)
11. [Les Exceptions](#11-les-exceptions)
12. [La Sécurité](#12-la-sécurité)
13. [Les Contrôleurs](#13-les-contrôleurs)
14. [Les Vues Thymeleaf](#14-les-vues-thymeleaf)
15. [Migrations Liquibase](#15-migrations-liquibase)
16. [Configuration Docker](#16-configuration-docker)
17. [CI/CD (GitHub Actions)](#17-cicd-github-actions)
18. [Diagramme de Base de Données](#18-diagramme-de-base-de-données)
19. [Tests de l'API](#19-tests-de-lapi)

---

## 1. Introduction

**Al-Baraka Digital** est une application bancaire digitale développée avec Spring Boot 4.0.0. Elle permet :
- La gestion des comptes bancaires
- Les opérations bancaires (dépôt, retrait, virement)
- La validation IA des documents justificatifs
- L'authentification JWT et OAuth2 (Google)
- Une interface web moderne avec Thymeleaf

### Technologies Utilisées
| Technologie | Version | Utilité |
|-------------|---------|---------|
| Java | 17 | Langage de programmation |
| Spring Boot | 4.0.0 | Framework principal |
| Spring Security | 7.0 | Authentification et autorisation |
| Spring Data JPA | 4.0.0 | Accès aux données |
| Hibernate | 7.x | ORM (Object-Relational Mapping) |
| MySQL | 8.0 | Base de données |
| Liquibase | 5.0 | Migrations de base de données |
| Thymeleaf | 3.1 | Moteur de templates |
| Lombok | 1.18 | Réduction du code boilerplate |
| JWT | 0.13 | Authentification par tokens |
| Docker | - | Conteneurisation |

---

## 2. Configuration du Projet (pom.xml)

Le fichier `pom.xml` est le cœur de la configuration Maven. Voici les dépendances principales :

### 2.1 Parent Spring Boot
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.0</version>
</parent>
```
👉 **Explication** : Hérite de la configuration par défaut de Spring Boot 4.0.0

### 2.2 Propriétés
```xml
<properties>
    <java.version>17</java.version>
</properties>
```
👉 **Explication** : Définit la version de Java utilisée

### 2.3 Dépendances Principales

#### a) Spring Web
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
👉 **Utilité** : Créer des API REST et des applications web

#### b) Spring Data JPA
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```
👉 **Utilité** : Accès simplifié à la base de données avec JPA/Hibernate

#### c) Spring Security
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
👉 **Utilité** : Sécurisation de l'application (authentification, autorisation)

#### d) OAuth2 Client
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```
👉 **Utilité** : Connexion via Google, Facebook, etc.

#### e) Thymeleaf
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>
```
👉 **Utilité** : Moteur de templates pour les pages HTML dynamiques

#### f) Validation
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```
👉 **Utilité** : Validation des données (@NotBlank, @Email, @Size, etc.)

#### g) Lombok
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```
👉 **Utilité** : Génère automatiquement les getters, setters, constructeurs, etc.

**Annotations Lombok utilisées :**
- `@Data` : Génère getters, setters, toString, equals, hashCode
- `@NoArgsConstructor` : Constructeur sans arguments
- `@AllArgsConstructor` : Constructeur avec tous les arguments
- `@Builder` : Pattern Builder pour créer des objets

#### h) MySQL Connector
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```
👉 **Utilité** : Connecteur pour la base de données MySQL

---

## 3. application.properties

Le fichier `application.properties` contient toutes les configurations de l'application.

**Fichier** : `src/main/resources/application.properties`

```properties
# Nom de l'application
spring.application.name=baraka

# Configuration MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/baraka
spring.datasource.username=root
spring.datasource.password=root

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=none              # Désactivé car on utilise Liquibase
spring.jpa.show-sql=true                        # Affiche les requêtes SQL
spring.jpa.properties.hibernate.format_sql=true # Formate les requêtes SQL
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Liquibase (Migrations)
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:/db/changelog/db.changelog-master.xml

# Upload de fichiers
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
file.upload-dir=uploads

# Spring AI (OpenAI) - Désactivé temporairement
spring.ai.openai.api-key=${SPRING_AI_OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-4o-mini
spring.ai.openai.chat.options.temperature=0.3
spring.ai.openai.chat.options.max-tokens=1000
```

### Explications des Propriétés

| Propriété | Valeur | Description |
|-----------|--------|-------------|
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/baraka` | URL de connexion MySQL |
| `spring.jpa.hibernate.ddl-auto` | `none` | Hibernate ne crée pas les tables (Liquibase s'en charge) |
| `spring.jpa.show-sql` | `true` | Affiche les requêtes SQL dans les logs |
| `spring.liquibase.enabled` | `true` | Active les migrations Liquibase |
| `spring.servlet.multipart.max-file-size` | `10MB` | Taille max des fichiers uploadés |
| `file.upload-dir` | `uploads` | Répertoire de stockage des documents |

---

## 4. Structure du Projet

```
src/main/java/com/Elbaraka/baraka/
├── BarakaApplication.java      # Classe principale
├── config/                     # Configuration
│   ├── SecurityConfig.java     # Configuration Spring Security
│   ├── JwtAuthenticationFilter.java
│   └── OAuth2Config.java
├── controller/                 # Contrôleurs REST et Web
│   ├── AuthController.java
│   ├── UserController.java
│   ├── AdminController.java
│   ├── AgentController.java
│   ├── ClientController.java
│   ├── DocumentController.java
│   └── AiValidationController.java
├── dto/                        # Data Transfer Objects
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   ├── OperationRequest.java
│   └── ...
├── entity/                     # Entités JPA
│   ├── User.java
│   ├── Account.java
│   ├── Operation.java
│   ├── Document.java
│   ├── Role.java
│   ├── Permission.java
│   └── AiValidationResult.java
├── enums/                      # Énumérations
│   ├── OperationType.java
│   ├── OperationStatus.java
│   ├── UserRole.java
│   └── AiDecision.java
├── exception/                  # Exceptions personnalisées
│   ├── ApiErrorResponse.java
│   ├── BusinessException.java
│   ├── EmailAlreadyExistsException.java
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── repository/                 # Repositories JPA
│   ├── UserRepository.java
│   ├── AccountRepository.java
│   ├── OperationRepository.java
│   └── ...
├── security/                   # Classes de sécurité
│   └── UserDetailsImpl.java
├── service/                    # Services métier
│   ├── UserService.java
│   ├── AuthService.java
│   ├── OperationService.java
│   ├── DocumentService.java
│   ├── AiValidationService.java
│   ├── CustomOAuth2UserService.java
│   └── impl/
│       └── UserServiceImpl.java
└── util/                       # Utilitaires
    └── JwtUtil.java
```

---

## 5. Les Entités (Entities)

Les entités sont des classes Java qui représentent les tables de la base de données.

### 4.1 User (Utilisateur)

**Fichier** : `entity/User.java`

**Description** : Représente un utilisateur du système (client, agent ou admin)

**Table** : `users`

```java
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Email doit être valide")
    @NotBlank(message = "Email est obligatoire")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Mot de passe est obligatoire")
    @Size(min = 4, message = "Le mot de passe doit contenir au moins 4 caractères")
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @NotBlank(message = "Nom complet est obligatoire")
    @Column(nullable = false)
    private String fullName;

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL)
    private Account account;
}
```

**Annotations expliquées** :
| Annotation | Explication |
|------------|-------------|
| `@Entity` | Marque la classe comme entité JPA |
| `@Table(name = "users")` | Nom de la table en base de données |
| `@Id` | Clé primaire |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | Auto-incrémentation |
| `@Column(nullable = false, unique = true)` | Colonne non nulle et unique |
| `@Email` | Validation : doit être un email valide |
| `@NotBlank` | Validation : ne doit pas être vide |
| `@Size(min = 4)` | Validation : minimum 4 caractères |
| `@JsonIgnore` | Exclut le champ de la sérialisation JSON |
| `@ManyToMany` | Relation plusieurs-à-plusieurs |
| `@JoinTable` | Table de jointure pour la relation |
| `@OneToOne(mappedBy = "owner")` | Relation un-à-un (côté inverse) |
| `@CreationTimestamp` | Date de création automatique |

---

### 4.2 Account (Compte Bancaire)

**Fichier** : `entity/Account.java`

**Description** : Représente un compte bancaire appartenant à un utilisateur

**Table** : `accounts`

```java
@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Numéro de compte est obligatoire")
    @Column(nullable = false, unique = true, length = 20)
    private String accountNumber;

    @NotNull(message = "Solde est obligatoire")
    @DecimalMin(value = "0.0", message = "Le solde ne peut pas être négatif")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    @JsonBackReference
    private User owner;
}
```

**Relations** :
- `@OneToOne` avec `User` : Un compte appartient à un seul utilisateur

**Annotations importantes** :
| Annotation | Explication |
|------------|-------------|
| `@DecimalMin(value = "0.0")` | Le solde ne peut pas être négatif |
| `precision = 15, scale = 2` | 15 chiffres au total, 2 décimales |
| `@JsonBackReference` | Évite la récursion infinie en JSON |

---

### 4.3 Operation (Opération Bancaire)

**Fichier** : `entity/Operation.java`

**Description** : Représente une opération bancaire (dépôt, retrait, virement)

**Table** : `operations`

```java
@Entity
@Table(name = "operations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Type d'opération est obligatoire")
    @Column(nullable = false)
    private OperationType type;

    @NotNull(message = "Montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Statut est obligatoire")
    @Column(nullable = false)
    private OperationStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime validatedAt;

    @Column
    private LocalDateTime executedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_source_id", nullable = false)
    private Account accountSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_destination_id")
    private Account accountDestination;

    @OneToMany(mappedBy = "operation", cascade = CascadeType.ALL)
    private List<Document> documents = new ArrayList<>();
}
```

**Relations** :
- `@ManyToOne` avec `Account` (source) : Le compte d'où provient l'argent
- `@ManyToOne` avec `Account` (destination) : Le compte qui reçoit l'argent (pour les virements)
- `@OneToMany` avec `Document` : Les documents justificatifs

**Annotations importantes** :
| Annotation | Explication |
|------------|-------------|
| `@Enumerated(EnumType.STRING)` | Stocke l'enum comme texte (pas comme nombre) |
| `cascade = CascadeType.ALL` | Supprime les documents si l'opération est supprimée |
| `orphanRemoval = true` | Supprime les documents orphelins |

---

### 4.4 Document (Document Justificatif)

**Fichier** : `entity/Document.java`

**Description** : Représente un document uploadé pour justifier une opération

**Table** : `documents`

```java
@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nom de fichier est obligatoire")
    @Column(nullable = false)
    private String fileName;

    @NotBlank(message = "Type de fichier est obligatoire")
    @Column(nullable = false)
    private String fileType;

    @NotBlank(message = "Chemin de stockage est obligatoire")
    @Column(nullable = false)
    private String storagePath;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_id", nullable = false)
    private Operation operation;
}
```

---

### 4.5 Role (Rôle)

**Fichier** : `entity/Role.java`

**Description** : Représente un rôle utilisateur (ADMIN, AGENT, CLIENT)

**Table** : `roles`

```java
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "roles_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions;
}
```

**Relations** :
- `@ManyToMany` avec `Permission` : Un rôle peut avoir plusieurs permissions

---

### 4.6 Permission

**Fichier** : `entity/Permission.java`

**Description** : Représente une permission spécifique

**Table** : `permissions`

```java
@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;
}
```

---

### 4.7 AiValidationResult (Résultat Validation IA)

**Fichier** : `entity/AiValidationResult.java`

**Description** : Stocke les résultats de l'analyse IA des documents

**Table** : `ai_validation_results`

```java
@Entity
@Table(name = "ai_validation_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiValidationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "operation_id", nullable = false, unique = true)
    private Operation operation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AiDecision decision;  // APPROVE, REJECT, NEED_HUMAN_REVIEW

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "analysis_summary", columnDefinition = "TEXT")
    private String analysisSummary;

    @Column(name = "extracted_amount", precision = 15, scale = 2)
    private BigDecimal extractedAmount;

    @Column(name = "document_quality_score")
    private Double documentQualityScore;

    @Column(name = "risk_factors", columnDefinition = "TEXT")
    private String riskFactors;

    @Column(name = "analyzed_at", nullable = false)
    private LocalDateTime analyzedAt;

    @Column(name = "model_used", length = 50)
    private String modelUsed;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;
}
```

---

## 6. Les Enums

### 6.1 OperationType

```java
public enum OperationType {
    DEPOSIT,    // Dépôt
    WITHDRAWAL, // Retrait
    TRANSFER    // Virement
}
```

### 6.2 OperationStatus

```java
public enum OperationStatus {
    PENDING,    // En attente de validation
    APPROVED,   // Approuvée
    REJECTED,   // Rejetée
    COMPLETED   // Complétée (pour < 10000 DH)
}
```

### 6.3 AiDecision

```java
public enum AiDecision {
    APPROVE,           // Approuver automatiquement
    REJECT,            // Rejeter automatiquement
    NEED_HUMAN_REVIEW  // Nécessite une vérification humaine
}
```

### 6.4 UserRole

```java
public enum UserRole {
    ROLE_ADMIN,
    ROLE_AGENT,
    ROLE_CLIENT
}
```

---

## 7. Les Repositories

Les repositories sont des interfaces qui permettent d'interagir avec la base de données.

### 7.1 UserRepository

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

👉 **Méthodes automatiques** :
- `findByEmail()` : Trouve un utilisateur par email
- `existsByEmail()` : Vérifie si un email existe

### 7.2 AccountRepository

```java
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    Optional<Account> findByOwnerId(Long ownerId);
}
```

### 7.3 OperationRepository

```java
@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
    List<Operation> findByAccountSourceIdOrAccountDestinationId(Long sourceId, Long destId);
    List<Operation> findByStatus(OperationStatus status);
    List<Operation> findByAccountSourceId(Long accountId);
}
```

### 7.4 DocumentRepository

```java
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByOperationId(Long operationId);
}
```

---

## 8. Les DTOs

Les DTOs (Data Transfer Objects) sont utilisés pour transférer des données entre les couches.

### 8.1 LoginRequest

```java
@Data
public class LoginRequest {
    @NotBlank(message = "Email est obligatoire")
    @Email(message = "Email doit être valide")
    private String email;

    @NotBlank(message = "Mot de passe est obligatoire")
    private String password;
}
```

### 8.2 RegisterRequest

```java
@Data
public class RegisterRequest {
    @NotBlank(message = "Email est obligatoire")
    @Email(message = "Email doit être valide")
    private String email;

    @NotBlank(message = "Mot de passe est obligatoire")
    @Size(min = 4, message = "Le mot de passe doit contenir au moins 4 caractères")
    private String password;

    @NotBlank(message = "Nom complet est obligatoire")
    private String fullName;
}
```

### 8.3 OperationRequest

```java
@Data
public class OperationRequest {
    @NotNull
    private OperationType type;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    private String destinationAccountNumber;  // Pour les virements
}
```

### 8.4 AuthResponse

```java
@Data
@Builder
public class AuthResponse {
    private String token;
    private String email;
    private String fullName;
    private List<String> roles;
}
```

---

## 9. Les Services

### 9.1 AuthService

**Fichier** : `service/AuthService.java`

```java
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        // 1. Vérifier si l'email existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        // 2. Créer l'utilisateur
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());

        // 3. Sauvegarder
        userRepository.save(user);

        // 4. Générer le token JWT
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .build();
    }
}
```

### 9.2 OperationService

**Fichier** : `service/OperationService.java`

```java
@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationRepository operationRepository;
    private final AccountRepository accountRepository;
    private static final BigDecimal THRESHOLD = new BigDecimal("10000");

    @Transactional
    public Operation createOperation(OperationRequest request, Long userId) {
        Account account = accountRepository.findByOwnerId(userId)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé"));

        Operation operation = new Operation();
        operation.setType(request.getType());
        operation.setAmount(request.getAmount());
        operation.setAccountSource(account);

        // Règle métier : Si montant >= 10000 DH → PENDING (validation requise)
        if (request.getAmount().compareTo(THRESHOLD) >= 0) {
            operation.setStatus(OperationStatus.PENDING);
        } else {
            operation.setStatus(OperationStatus.COMPLETED);
            executeOperation(operation);
        }

        return operationRepository.save(operation);
    }

    private void executeOperation(Operation operation) {
        Account source = operation.getAccountSource();

        switch (operation.getType()) {
            case DEPOSIT:
                source.setBalance(source.getBalance().add(operation.getAmount()));
                break;
            case WITHDRAWAL:
                if (source.getBalance().compareTo(operation.getAmount()) < 0) {
                    throw new RuntimeException("Solde insuffisant");
                }
                source.setBalance(source.getBalance().subtract(operation.getAmount()));
                break;
            case TRANSFER:
                // Logique de virement...
                break;
        }
    }
}
```

### 9.3 AiValidationService

**Fichier** : `service/AiValidationService.java`

```java
@Service
@Slf4j
public class AiValidationService {

    private final AiValidationResultRepository aiValidationResultRepository;
    private final DocumentRepository documentRepository;
    private final Tika tika = new Tika();

    @Transactional
    public AiValidationResult analyzeOperation(Operation operation) {
        log.info("Démarrage de l'analyse IA pour l'opération #{}", operation.getId());

        // 1. Récupérer les documents
        List<Document> documents = documentRepository.findByOperationId(operation.getId());

        // 2. Extraire le texte des documents avec Apache Tika
        String extractedText = extractTextFromDocument(documents.get(0));

        // 3. Analyser avec l'IA (Spring AI - OpenAI)
        // Note: Temporairement désactivé - incompatible avec Spring Boot 4.0.0

        // 4. Retourner le résultat
        return AiValidationResult.builder()
                .operation(operation)
                .decision(AiDecision.NEED_HUMAN_REVIEW)
                .confidenceScore(0.0)
                .analysisSummary("Validation manuelle requise")
                .analyzedAt(LocalDateTime.now())
                .build();
    }

    private String extractTextFromDocument(Document document) {
        try {
            File file = new File(document.getStoragePath());
            return tika.parseToString(file);
        } catch (Exception e) {
            log.error("Erreur extraction texte", e);
            return null;
        }
    }
}
```

### 9.4 UserService & UserServiceImpl

**Interface** : `service/UserService.java`

```java
public interface UserService {
    User createUser(User user);
    User getUserById(Long id);
    User getUserByEmail(String email);
    List<User> getAllUsers();
    User updateUser(Long id, User user);
    void deactivateUser(Long id);
    void changeUserRole(Long id, String roleName);
}
```

**Implémentation** : `service/impl/UserServiceImpl.java`

```java
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public User createUser(User user) {
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email déjà utilisé");
        }
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }

    @Override
    public void deactivateUser(Long id) {
        User user = getUserById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public void changeUserRole(Long id, String roleName) {
        User user = getUserById(id);
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé: " + roleName));
        
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);
    }
}
```

### 9.5 DocumentService

**Fichier** : `service/DocumentService.java`

```java
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final OperationRepository operationRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Transactional
    public Document uploadDocument(Long operationId, MultipartFile file) {
        // 1. Vérifier que l'opération existe
        Operation operation = operationRepository.findById(operationId)
                .orElseThrow(() -> new ResourceNotFoundException("Opération non trouvée"));

        // 2. Valider le fichier (non vide, type autorisé)
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        String contentType = file.getContentType();
        if (!isValidFileType(contentType)) {
            throw new IllegalArgumentException("Type de fichier non autorisé");
        }

        // 3. Générer un nom unique et sauvegarder
        String uniqueFilename = UUID.randomUUID().toString() + getExtension(file);
        Path filePath = Paths.get(uploadDir).resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 4. Créer l'entité Document
        Document document = new Document();
        document.setFileName(file.getOriginalFilename());
        document.setFileType(contentType);
        document.setStoragePath(uniqueFilename);
        document.setOperation(operation);

        return documentRepository.save(document);
    }

    @Transactional(readOnly = true)
    public Resource downloadDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document non trouvé"));

        Path filePath = Paths.get(uploadDir).resolve(document.getStoragePath());
        return new UrlResource(filePath.toUri());
    }

    private boolean isValidFileType(String contentType) {
        return contentType != null && (
            contentType.equals("application/pdf") ||
            contentType.equals("image/jpeg") ||
            contentType.equals("image/png")
        );
    }
}
```

👉 **Fonctionnalités** :
- Upload de documents (PDF, JPG, PNG)
- Validation du type de fichier
- Génération de noms uniques (UUID)
- Téléchargement de documents

### 9.6 CustomOAuth2UserService

**Fichier** : `service/CustomOAuth2UserService.java`

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Charger l'utilisateur OAuth2 (Google)
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        // 2. Extraire les informations du profil
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        
        log.info("OAuth2 login attempt for email: {}", email);
        
        // 3. Trouver ou créer l'utilisateur
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(email, name));
        
        // 4. Retourner l'utilisateur OAuth2 avec les autorités
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_CLIENT")),
                attributes,
                "email"  // Attribut utilisé comme identifiant
        );
    }
    
    private User createNewUser(String email, String fullName) {
        log.info("Creating new user from OAuth2: {}", email);
        
        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName != null ? fullName : "OAuth2 User");
        user.setPassword("");  // Pas de mot de passe pour OAuth2
        user.setActive(true);
        
        return userRepository.save(user);
    }
}
```

👉 **Fonctionnalités** :
- Connexion via Google OAuth2
- Création automatique du compte si inexistant
- Attribution du rôle CLIENT par défaut

---

## 10. Les Utilitaires (JwtUtil)

**Fichier** : `util/JwtUtil.java`

La classe `JwtUtil` gère la génération et la validation des tokens JWT.

```java
@Component
public class JwtUtil {

    // Clé secrète pour signer les tokens (à stocker dans les variables d'environnement en prod)
    public static final String SECRET = "g4pSkYBCvp7WkBT/pfNkkt/KffRXhu+hiQXutH2U6nQ=";
    
    /**
     * Génère un token JWT pour un utilisateur
     */
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)                                    // Email comme sujet
                .setIssuedAt(new Date())                              // Date de création
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))  // Expire dans 30 min
                .signWith(getSignKey(), SignatureAlgorithm.HS256)     // Signature HS256
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extrait l'email du token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait la date d'expiration
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSignKey())
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }

    /**
     * Vérifie si le token est expiré
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Valide le token
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
```

### Méthodes Clés

| Méthode | Description |
|---------|-------------|
| `generateToken(email)` | Crée un nouveau token JWT |
| `extractUsername(token)` | Extrait l'email du token |
| `validateToken(token, userDetails)` | Valide le token et vérifie l'utilisateur |
| `isTokenExpired(token)` | Vérifie si le token est expiré |

### Configuration du Token

| Paramètre | Valeur | Description |
|-----------|--------|-------------|
| Algorithme | HS256 | HMAC avec SHA-256 |
| Durée de vie | 30 minutes | `1000 * 60 * 30` ms |
| Sujet | Email | Identifiant de l'utilisateur |

---

## 11. Les Exceptions

Le package `exception` contient les classes pour gérer les erreurs de manière uniforme.

### 11.1 ApiErrorResponse

Classe DTO pour formater les réponses d'erreur :

```java
@Data
@AllArgsConstructor
public class ApiErrorResponse {
    private String timestamp;   // Date/heure de l'erreur
    private int status;         // Code HTTP (404, 409, etc.)
    private String error;       // Type d'erreur (Not Found, Conflict)
    private String message;     // Message détaillé
    private String path;        // URL de la requête
}
```

### 11.2 ResourceNotFoundException

Exception levée quand une ressource n'est pas trouvée (HTTP 404) :

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

### 11.3 BusinessException

Exception pour les erreurs métier (HTTP 409) :

```java
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
```

### 11.4 EmailAlreadyExistsException

Exception levée quand un email est déjà utilisé :

```java
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
```

### 11.5 GlobalExceptionHandler

Gestionnaire global qui intercepte toutes les exceptions :

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailExists(EmailAlreadyExistsException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access Denied: You do not have the required role.", request);
    }
}
```

### Tableau des Exceptions

| Exception | Code HTTP | Utilisation |
|-----------|-----------|-------------|
| `ResourceNotFoundException` | 404 | Ressource introuvable |
| `BusinessException` | 409 | Erreur de logique métier |
| `EmailAlreadyExistsException` | 409 | Email déjà enregistré |
| `AccessDeniedException` | 403 | Accès refusé (rôle insuffisant) |

---

## 12. La Sécurité

### 12.1 SecurityConfig

**Fichier** : `config/SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Pages publiques
                .requestMatchers("/", "/login", "/register", "/api/auth/**").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                
                // Pages admin
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // Pages agent
                .requestMatchers("/agent/**").hasAnyRole("ADMIN", "AGENT")
                
                // Pages client
                .requestMatchers("/client/**").hasAnyRole("ADMIN", "AGENT", "CLIENT")
                
                // Toute autre requête nécessite une authentification
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 12.2 JwtAuthenticationFilter

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                    );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### 12.3 OAuth2Config

**Fichier** : `config/OAuth2Config.java`

Configuration pour l'authentification Google OAuth2 :

```java
@Configuration
public class OAuth2Config {
    
    /**
     * Configure Google OAuth2 client registration.
     * Requires environment variables:
     * - GOOGLE_CLIENT_ID
     * - GOOGLE_CLIENT_SECRET
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
```

### Configuration Google Cloud Console

Pour configurer OAuth2 avec Google :

1. Aller sur [Google Cloud Console](https://console.cloud.google.com/)
2. Créer un nouveau projet ou sélectionner un existant
3. Aller dans **APIs & Services** → **Credentials**
4. Cliquer sur **Create Credentials** → **OAuth 2.0 Client IDs**
5. Configurer :
   - **Application type** : Web application
   - **Authorized redirect URIs** : `http://localhost:8080/login/oauth2/code/google`
6. Copier le **Client ID** et **Client Secret**
7. Les définir comme variables d'environnement :

```bash
export GOOGLE_CLIENT_ID="your-client-id"
export GOOGLE_CLIENT_SECRET="your-client-secret"
```

---

## 13. Les Contrôleurs

### 13.1 AuthController (API REST)

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
```

### 13.2 ClientController (Web Thymeleaf)

```java
@Controller
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {

    private final OperationService operationService;
    private final AccountRepository accountRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails user) {
        Account account = accountRepository.findByOwnerEmail(user.getUsername())
                .orElseThrow();
        
        model.addAttribute("account", account);
        model.addAttribute("operations", operationService.getOperationsForAccount(account.getId()));
        
        return "client/dashboard";
    }

    @PostMapping("/operations")
    public String createOperation(@Valid @ModelAttribute OperationRequest request,
                                  @AuthenticationPrincipal UserDetails user) {
        operationService.createOperation(request, user);
        return "redirect:/client/dashboard";
    }
}
```

### 13.3 AdminController

```java
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return "redirect:/admin/users";
    }
}
```

### 13.4 AgentController

```java
@Controller
@RequestMapping("/agent")
@RequiredArgsConstructor
public class AgentController {

    private final OperationService operationService;

    @GetMapping("/pending")
    public String pendingOperations(Model model) {
        model.addAttribute("operations", operationService.getPendingOperations());
        return "agent/pending";
    }

    @PostMapping("/operations/{id}/approve")
    public String approveOperation(@PathVariable Long id) {
        operationService.approveOperation(id);
        return "redirect:/agent/pending";
    }

    @PostMapping("/operations/{id}/reject")
    public String rejectOperation(@PathVariable Long id) {
        operationService.rejectOperation(id);
        return "redirect:/agent/pending";
    }
}
```

---

## 14. Les Vues Thymeleaf

### Structure des Templates

```
src/main/resources/templates/
├── layout.html              # Template principal (header, footer)
├── login.html               # Page de connexion
├── register.html            # Page d'inscription
├── admin/
│   ├── dashboard.html       # Dashboard admin
│   └── users.html           # Gestion des utilisateurs
├── agent/
│   ├── dashboard.html       # Dashboard agent
│   └── pending.html         # Opérations en attente
└── client/
    ├── dashboard.html       # Dashboard client
    ├── operations.html      # Liste des opérations
    └── transfer.html        # Formulaire de virement
```

### Exemple : login.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Connexion - Al-Baraka Digital</title>
    <link rel="stylesheet" th:href="@{/css/style.css}"/>
</head>
<body>
    <div class="login-container">
        <h1>Connexion</h1>
        
        <form th:action="@{/login}" method="post">
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="username" required/>
            </div>
            
            <div class="form-group">
                <label for="password">Mot de passe</label>
                <input type="password" id="password" name="password" required/>
            </div>
            
            <button type="submit">Se connecter</button>
        </form>
        
        <div class="oauth-login">
            <a th:href="@{/oauth2/authorization/google}" class="google-btn">
                Connexion avec Google
            </a>
        </div>
        
        <p>Pas encore de compte? <a th:href="@{/register}">S'inscrire</a></p>
    </div>
</body>
</html>
```

---

## 15. Migrations Liquibase

### Structure des Fichiers

```
src/main/resources/db/changelog/
├── db.changelog-master.xml       # Fichier principal
└── changes/
    ├── 001-create-users-table.xml
    ├── 002-create-accounts-table.xml
    ├── 003-create-operations-table.xml
    ├── 004-create-documents-table.xml
    ├── 005-insert-fake-data-users-table.xml
    ├── 006-create-permissions-table.xml
    ├── 007-create-roles-table.xml
    ├── 008-create-roles-permissions-table.xml
    ├── 009-create-users-roles-table.xml
    ├── 010-insert-permissions-and-roles.xml
    ├── 011-remove-role-column-from-users.xml
    └── 012-create-ai-validation-results-table.xml
```

### Exemple : 001-create-users-table.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog">

    <changeSet id="001-create-users-table" author="al-baraka-digital">
        <createTable tableName="users">
            <column name="id" type="BIGINT" autoIncrement="true">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="email" type="VARCHAR(255)">
                <constraints nullable="false" unique="true"/>
            </column>
            <column name="password" type="VARCHAR(255)">
                <constraints nullable="false"/>
            </column>
            <column name="full_name" type="VARCHAR(255)">
                <constraints nullable="false"/>
            </column>
            <column name="active" type="BOOLEAN" defaultValueBoolean="true">
                <constraints nullable="false"/>
            </column>
            <column name="created_at" type="TIMESTAMP" defaultValueComputed="CURRENT_TIMESTAMP">
                <constraints nullable="false"/>
            </column>
        </createTable>
    </changeSet>

</databaseChangeLog>
```

---

## 16. Configuration Docker

### 16.1 Dockerfile

```dockerfile
# Étape 1: Build
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# Étape 2: Runtime
FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN mkdir -p /app/uploads && chown -R spring:spring /app
USER spring:spring
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### 16.2 docker-compose.yml

```yaml
version: "3.8"

services:
  mysql:
    image: mysql:8.0
    container_name: albaraka-mysql
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: baraka
      MYSQL_USER: baraka_user
      MYSQL_PASSWORD: baraka_password
    ports:
      - "3307:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  app:
    build: .
    container_name: albaraka-app
    depends_on:
      mysql:
        condition: service_healthy
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/baraka
      SPRING_DATASOURCE_USERNAME: baraka_user
      SPRING_DATASOURCE_PASSWORD: baraka_password
    ports:
      - "8081:8080"
    volumes:
      - uploads:/app/uploads

  nginx:
    image: nginx:alpine
    container_name: albaraka-nginx
    depends_on:
      - app
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro

volumes:
  mysql_data:
  uploads:
```

### 16.3 Commandes Docker

```bash
# Construire et démarrer
docker-compose up --build -d

# Voir les logs
docker-compose logs -f app

# Arrêter
docker-compose down

# Supprimer les volumes (réinitialiser la BDD)
docker-compose down -v
```

---

## 17. CI/CD (GitHub Actions)

Le projet utilise GitHub Actions pour l'intégration et le déploiement continus.

### 17.1 Structure des Workflows

```
.github/workflows/
├── build.yml       # Build et tests
├── code-quality.yml # Qualité du code
├── deploy.yml      # Déploiement production
└── docker.yml      # Build Docker
```

### 17.2 build.yml - Build et Tests

```yaml
name: Build and Test

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  build:
    name: Build with Maven
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
        
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: 'maven'
          
      - name: Cache Maven packages
        uses: actions/cache@v3
        with:
          path: ~/.m2/repository
          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
          
      - name: Build with Maven
        run: mvn -B clean package -DskipTests
        
      - name: Run tests
        run: mvn -B test
        env:
          SPRING_AI_OPENAI_API_KEY: ${{ secrets.SPRING_AI_OPENAI_API_KEY }}
          GOOGLE_CLIENT_ID: ${{ secrets.GOOGLE_CLIENT_ID }}
          GOOGLE_CLIENT_SECRET: ${{ secrets.GOOGLE_CLIENT_SECRET }}
          
      - name: Generate test coverage report
        run: mvn -B jacoco:report
        
      - name: Upload build artifact
        uses: actions/upload-artifact@v3
        with:
          name: albaraka-jar
          path: target/*.jar
```

### 17.3 deploy.yml - Déploiement

```yaml
name: Deploy to Production

on:
  push:
    branches: [ main ]
    tags:
      - 'v*'
  workflow_dispatch:
    inputs:
      environment:
        description: 'Deployment environment'
        required: true
        default: 'production'

jobs:
  deploy:
    name: Deploy Application
    runs-on: ubuntu-latest
    environment: 
      name: ${{ github.event.inputs.environment || 'production' }}
      url: https://albaraka.digital
      
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
        
      - name: Setup SSH
        uses: webfactory/ssh-agent@v0.8.0
        with:
          ssh-private-key: ${{ secrets.SSH_PRIVATE_KEY }}
          
      - name: Create .env file
        run: |
          cat << EOF > .env.production
          SPRING_AI_OPENAI_API_KEY=${{ secrets.SPRING_AI_OPENAI_API_KEY }}
          GOOGLE_CLIENT_ID=${{ secrets.GOOGLE_CLIENT_ID }}
          GOOGLE_CLIENT_SECRET=${{ secrets.GOOGLE_CLIENT_SECRET }}
          MYSQL_ROOT_PASSWORD=${{ secrets.MYSQL_ROOT_PASSWORD }}
          MYSQL_DATABASE=baraka
          SPRING_PROFILES_ACTIVE=prod
          EOF
          
      - name: Copy files to server
        run: |
          scp -r docker-compose.yml .env.production nginx/ \
            ${{ secrets.SERVER_USER }}@${{ secrets.SERVER_HOST }}:/opt/albaraka/
            
      - name: Deploy with Docker Compose
        run: |
          ssh ${{ secrets.SERVER_USER }}@${{ secrets.SERVER_HOST }} \
            "cd /opt/albaraka && docker-compose pull && docker-compose up -d"
```

### 17.4 Secrets GitHub à Configurer

| Secret | Description |
|--------|-------------|
| `SPRING_AI_OPENAI_API_KEY` | Clé API OpenAI |
| `GOOGLE_CLIENT_ID` | ID client Google OAuth2 |
| `GOOGLE_CLIENT_SECRET` | Secret client Google OAuth2 |
| `SSH_PRIVATE_KEY` | Clé SSH pour le serveur |
| `SERVER_HOST` | IP/hostname du serveur |
| `SERVER_USER` | Utilisateur SSH |
| `MYSQL_ROOT_PASSWORD` | Mot de passe MySQL root |

---

## 18. Diagramme de Base de Données

### 18.1 Schéma Relationnel

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│     users       │       │    accounts     │       │   operations    │
├─────────────────┤       ├─────────────────┤       ├─────────────────┤
│ id (PK)         │───┐   │ id (PK)         │   ┌───│ id (PK)         │
│ email           │   │   │ account_number  │   │   │ type            │
│ password        │   │   │ balance         │   │   │ amount          │
│ full_name       │   └──►│ owner_id (FK)   │◄──┘   │ status          │
│ active          │       └─────────────────┘   │   │ created_at      │
│ created_at      │                             │   │ validated_at    │
└────────┬────────┘                             │   │ executed_at     │
         │                                      │   │ account_source_id (FK)
         │                                      │   │ account_dest_id (FK)
         │                                      │   └────────┬────────┘
         │                                      │            │
┌────────▼────────┐                             │   ┌────────▼────────┐
│   users_roles   │                             │   │    documents    │
├─────────────────┤                             │   ├─────────────────┤
│ user_id (FK)    │                             │   │ id (PK)         │
│ role_id (FK)    │                             │   │ file_name       │
└────────┬────────┘                             │   │ file_type       │
         │                                      │   │ storage_path    │
┌────────▼────────┐                             │   │ uploaded_at     │
│     roles       │                             │   │ operation_id (FK)
├─────────────────┤                             │   └─────────────────┘
│ id (PK)         │                             │
│ name            │                             │   ┌─────────────────┐
│ description     │                             │   │ai_validation_   │
└────────┬────────┘                             │   │results          │
         │                                      │   ├─────────────────┤
┌────────▼────────┐                             │   │ id (PK)         │
│roles_permissions│                             └───│ operation_id (FK)
├─────────────────┤                                 │ decision        │
│ role_id (FK)    │                                 │ confidence_score│
│ permission_id (FK)                                │ analysis_summary│
└────────┬────────┘                                 │ extracted_amount│
         │                                          │ analyzed_at     │
┌────────▼────────┐                                 └─────────────────┘
│   permissions   │
├─────────────────┤
│ id (PK)         │
│ name            │
│ description     │
└─────────────────┘
```

### 18.2 Relations

| Table Source | Relation | Table Cible | Description |
|--------------|----------|-------------|-------------|
| `users` | 1:1 | `accounts` | Un utilisateur a un compte |
| `users` | N:M | `roles` | Un utilisateur peut avoir plusieurs rôles |
| `roles` | N:M | `permissions` | Un rôle a plusieurs permissions |
| `accounts` | 1:N | `operations` | Un compte a plusieurs opérations |
| `operations` | 1:N | `documents` | Une opération a plusieurs documents |
| `operations` | 1:1 | `ai_validation_results` | Une opération a un résultat IA |

### 18.3 Tables de Jointure

- **users_roles** : Lie les utilisateurs aux rôles
- **roles_permissions** : Lie les rôles aux permissions

---

## 19. Tests de l'API

### 19.1 Inscription

```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "fullName": "Test User"
  }'
```

### 19.2 Connexion

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### 19.3 Créer une Opération (avec JWT)

```bash
curl -X POST http://localhost:8081/api/operations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{
    "type": "DEPOSIT",
    "amount": 5000.00
  }'
```

---

## 📌 Résumé du Workflow

```
1. Utilisateur s'inscrit/connecte (AuthController)
        ↓
2. JWT généré et retourné
        ↓
3. Utilisateur crée une opération (OperationController)
        ↓
4. Si montant >= 10000 DH → Status = PENDING
        ↓
5. Agent voit les opérations en attente
        ↓
6. Validation IA des documents (AiValidationService)
        ↓
7. Agent approuve/rejette
        ↓
8. Opération exécutée, soldes mis à jour
```

---

## 🚀 Commandes de Lancement

```bash
# Développement local
./mvnw spring-boot:run

# Production Docker
docker-compose up --build -d

# Accès
- Application: http://localhost:8081
- Nginx Proxy: http://localhost:80
```

---

**Auteur** : Al-Baraka Digital Team  
**Date** : Janvier 2026  
**Version** : 2.0.0
