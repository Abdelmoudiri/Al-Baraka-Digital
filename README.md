# Al-Baraka-Digital 🏦

**Al Baraka Digital** est une application bancaire numérique moderne et sécurisée conçue pour gérer les opérations financières destinées aux clients et aux agents internes de la banque.

---

## 🚀 Fonctionnalités

### ✅ Implémenté

#### 🔐 Authentification & Sécurité
- ✅ Inscription client avec création automatique de compte
- ✅ Authentification JWT (token valide 24h)
- ✅ Gestion des rôles (CLIENT, AGENT, ADMIN)
- ✅ Mot de passe hashé avec BCrypt
- ✅ Protection des endpoints par rôle

#### 💰 Opérations Bancaires (CLIENT)
- ✅ **Dépôt** : Auto-validation si ≤ 10 000 DH
- ✅ **Retrait** : Vérification solde + auto-validation si ≤ 10 000 DH
- ✅ **Virement** : Transfert entre comptes avec validation
- ✅ **Historique** : Consultation de toutes les opérations
- ✅ **Profil** : Consultation solde et informations compte

#### 👨‍💼 Workflow Validation (AGENT)
- ✅ Consultation des opérations en attente (PENDING)
- ✅ Approbation des opérations > 10 000 DH
- ✅ Rejet des opérations
- ✅ Mise à jour automatique des soldes après validation

#### 🔧 Administration (ADMIN)
- ✅ Liste de tous les utilisateurs
- ✅ Activation/désactivation de comptes
- ✅ Consultation de toutes les opérations

#### 📎 Gestion des Documents
- ✅ Upload de justificatifs (PDF, JPG, PNG - max 10 MB)
- ✅ Téléchargement de documents
- ✅ Liste des documents par opération
- ✅ Suppression de documents
- ✅ Validation obligatoire de documents pour opérations > 10 000 DH

### 🔴 À Implémenter
- ⏳ Tests unitaires et d'intégration
- ⏳ Documentation Swagger/OpenAPI
- ⏳ Dockerisation complète (MySQL + App)

---

## 🛠️ Technologies

- **Backend** : Spring Boot 4.0.0
- **Sécurité** : Spring Security + JWT
- **Base de données** : MySQL 8.0
- **ORM** : Hibernate/JPA
- **Migration** : Liquibase
- **Build** : Gradle 8.14.3
- **Java** : 17

---

## 📦 Installation

### Prérequis
- Java 17+
- MySQL 8.0+
- Gradle 8.14+ (ou utiliser le wrapper `./gradlew`)

### 1. Cloner le projet
```bash
git clone <repository-url>
cd Al-Baraka-Digital
```

### 2. Configurer MySQL
```bash
# Créer la base de données
mysql -u root -p
CREATE DATABASE baraka;
CREATE USER 'baraka_user'@'localhost' IDENTIFIED BY 'baraka_password';
GRANT ALL PRIVILEGES ON baraka.* TO 'baraka_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 3. Configurer application.properties
Modifier `src/main/resources/application.properties` :
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/baraka
spring.datasource.username=baraka_user
spring.datasource.password=baraka_password
```

### 4. Lancer l'application
```bash
# Avec Gradle Wrapper
./gradlew bootRun

# Ou build puis run
./gradlew build
java -jar build/libs/baraka-0.0.1-SNAPSHOT.jar
```

L'application démarre sur **http://localhost:8080**

---

## 📚 API Endpoints

### 🔓 Publics (sans authentification)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/auth/register` | Inscription client |
| POST | `/auth/login` | Connexion (retourne JWT) |

### 👤 Client (Rôle: CLIENT)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/client/profile` | Profil et solde |
| POST | `/api/client/operations/deposit` | Dépôt |
| POST | `/api/client/operations/withdrawal` | Retrait |
| POST | `/api/client/operations/transfer` | Virement |
| GET | `/api/client/operations` | Historique |
| POST | `/api/client/operations/{id}/documents` | Upload justificatif |
| DELETE | `/api/client/documents/{id}` | Supprimer document |

### 📎 Documents (Tous les rôles authentifiés)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/operations/{id}/documents` | Liste documents d'une opération |
| GET | `/api/documents/{id}/download` | Télécharger document |

### 👨‍💼 Agent Bancaire (Rôle: AGENT)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/agent/operations/pending` | Opérations en attente |
| PUT | `/api/agent/operations/{id}/approve` | Approuver |
| PUT | `/api/agent/operations/{id}/reject` | Rejeter |
| GET | `/api/agent/operations` | Toutes les opérations |

### 🔧 Administration (Rôle: ADMIN)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/admin/users` | Liste utilisateurs |
| PUT | `/api/admin/users/{id}/activate` | Activer compte |
| PUT | `/api/admin/users/{id}/deactivate` | Désactiver compte |
| GET | `/api/admin/operations` | Toutes les opérations |

**📖 Documentation complète** : Voir [API_TESTS.md](API_TESTS.md)

---

## 🔐 Sécurité JWT

### Workflow d'authentification

1. **Login** → Génère token JWT (exp: 24h)
2. **Requête** → Inclure header : `Authorization: Bearer <token>`
3. **Validation** → JwtAuthenticationFilter vérifie le token

### Architecture Sécurité

```
Client Request
    ↓
JwtAuthenticationFilter (validate token)
    ↓
SecurityContextHolder (set authentication)
    ↓
@PreAuthorize("hasRole('...')")
    ↓
Controller → Service → Repository
```

---

## 💡 Règles Métier

### Validation Automatique des Opérations

| Montant | Statut | Validation | Solde | Documents |
|---------|--------|------------|-------|-----------|
| ≤ 10 000 DH | COMPLETED | ✅ Automatique | ✅ Mis à jour immédiatement | ❌ Non requis |
| > 10 000 DH | PENDING | ⏳ Requiert approbation agent | ❌ Inchangé jusqu'à approbation | ✅ **Obligatoire** |

### Documents Justificatifs
- **Types acceptés** : PDF, JPG, PNG
- **Taille maximale** : 10 MB
- **Règle** : Au moins 1 document requis pour approuver une opération > 10 000 DH
- **Stockage** : Répertoire `uploads/` (configurable)

### Statuts d'Opération
- **PENDING** : En attente de validation agent
- **APPROVED** : Approuvée par agent (solde mis à jour)
- **REJECTED** : Rejetée par agent (solde inchangé)
- **COMPLETED** : Complétée automatiquement (≤ 10 000 DH)

---

## 🗄️ Base de Données

### Schéma Principal

```sql
-- Utilisateurs
users (id, email, password, full_name, active, created_at)

-- Comptes bancaires
accounts (id, account_number, balance, owner_id)

-- Opérations
operations (id, type, amount, status, created_at, executed_at, 
            account_source_id, account_destination_id)

-- Documents justificatifs
documents (id, file_name, file_type, storage_path, uploaded_at, operation_id)

-- Rôles & Permissions
roles (id, name, description)
permissions (id, name, description)
users_roles (user_id, role_id)
roles_permissions (role_id, permission_id)

-- Documents (à implémenter)
documents (id, file_name, file_type, storage_path, operation_id)
```

**Gestion des migrations** : Liquibase (15 changesets)

---

## 🧪 Tests

### Test rapide
```bash
# 1. Inscription
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User"
  }'

# 2. Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'

# 3. Profil (avec TOKEN reçu du login)
curl -X GET http://localhost:8080/api/client/profile \
  -H "Authorization: Bearer <TOKEN>"
```

---

## 📂 Structure du Projet

```
src/main/java/com/Elbaraka/baraka/
├── config/          # Configuration Spring Security
├── controller/      # REST Controllers
│   ├── AuthController.java
│   ├── ClientController.java
│   ├── AgentController.java
│   └── AdminController.java
├── dto/             # Data Transfer Objects
├── entity/          # Entités JPA
│   ├── User.java
│   ├── Account.java
│   ├── Operation.java
│   ├── Role.java
│   └── Permission.java
├── enums/           # Enums
│   ├── OperationType.java
│   └── OperationStatus.java
├── repository/      # Repositories JPA
├── security/        # JWT Utils & Filters
├── service/         # Services métier
│   ├── AuthService.java
│   ├── UserService.java
│   └── OperationService.java
└── util/            # Utilitaires
```

---

## 🐳 Docker (En cours)

### Build & Run avec Docker Compose
```bash
# À implémenter
docker-compose up -d
```

---

## 📝 Licence

MIT License

---

## 👥 Contributeurs

- **Équipe Al-Baraka Digital**

---

## 📞 Support

Pour toute question ou problème :
- 📧 Email : support@albaraka-digital.com
- 📖 Documentation : [API_TESTS.md](API_TESTS.md)
- 📋 JIRA Planning : [JIRA_PLANNING.md](JIRA_PLANNING.md)