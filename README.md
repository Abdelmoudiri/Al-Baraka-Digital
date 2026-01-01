# 🏦 Al-Baraka Digital Banking Platform

[![Build Status](https://github.com/Abdelmoudiri/Al-Baraka-Digital/actions/workflows/build.yml/badge.svg)](https://github.com/Abdelmoudiri/Al-Baraka-Digital/actions)
[![Docker](https://github.com/Abdelmoudiri/Al-Baraka-Digital/actions/workflows/docker.yml/badge.svg)](https://github.com/Abdelmoudiri/Al-Baraka-Digital/actions)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java Version](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg)](https://spring.io/projects/spring-boot)

**Al Baraka Digital** est une plateforme bancaire digitale moderne conforme à la Charia islamique, intégrant l'Intelligence Artificielle pour la validation automatique des opérations bancaires.

## ✨ Fonctionnalités Principales

### 🤖 Intelligence Artificielle
- **Validation Automatique** : Analyse des documents avec OpenAI GPT-4o-mini
- **Extraction de Texte** : Apache Tika pour PDF et images
- **Score de Confiance** : Décisions APPROVE/REJECT/NEED_HUMAN_REVIEW
- **Analyse de Risques** : Détection automatique des facteurs de risque

### 🔐 Authentification & Sécurité
- **JWT Authentication** : Tokens sécurisés avec expiration
- **OAuth2 Google** : Connexion sociale simplifiée
- **Remember-Me** : Sessions persistantes (30 jours)
- **RBAC** : Rôles CLIENT, AGENT, ADMIN avec permissions granulaires
- **BCrypt Password** : Hashage sécurisé des mots de passe

### 💰 Opérations Bancaires
- **Dépôts & Retraits** : Validation automatique ≤ 10 000 DH
- **Virements** : Transferts entre comptes
- **Historique** : Consultation des opérations avec filtres
- **Documents** : Upload de justificatifs (PDF, JPG, PNG)
- **Validation IA** : Analyse automatique des documents

### 👨‍💼 Espace Agent
- **Dashboard** : Vue d'ensemble des opérations en attente
- **Validation Manuelle** : Approbation/Rejet avec justification
- **Statistiques IA** : Métriques de performance de l'IA
- **Révision Humaine** : Cas nécessitant intervention humaine

### 🎨 Interface Utilisateur
- **Thymeleaf Templates** : Pages HTML dynamiques
- **Bootstrap 5** : Design responsive moderne
- **Dashboards** : Client, Agent, Admin personnalisés
- **Multi-langue** : Support Français/Arabe (à venir)

## 🛠️ Stack Technique

### Backend
- **Java 17** - Langage de programmation
- **Spring Boot 4.0.0** - Framework application
- **Spring Security 6** - Authentification & Autorisation
- **Spring Data JPA** - Persistance des données
- **Spring AI 1.0.0-M4** - Intégration OpenAI
- **Liquibase** - Gestion migrations BDD

### Frontend
- **Thymeleaf** - Moteur de templates
- **Bootstrap 5.3** - Framework CSS
- **Bootstrap Icons** - Icônes

### Database
- **MySQL 8.0** - Base de données relationnelle

### Outils & Libraries
- **Apache Tika 2.9.1** - Extraction texte documents
- **Lombok** - Réduction boilerplate
- **MapStruct** - Mapping DTO/Entity
- **JJWT 0.13.0** - Gestion tokens JWT

### DevOps
- **Docker** - Containerisation
- **Docker Compose** - Orchestration
- **Nginx** - Reverse proxy
- **GitHub Actions** - CI/CD

## 📦 Installation

### Prérequis
- Java 17+
- Maven 3.9+
- MySQL 8.0+
- Docker & Docker Compose (optionnel)

### 1. Cloner le Repository
```bash
git clone https://github.com/Abdelmoudiri/Al-Baraka-Digital.git
cd Al-Baraka-Digital
```

### 2. Configuration Environnement
Créer un fichier `.env` basé sur `.env.example` :

```bash
cp .env.example .env
```

Remplir les variables obligatoires :
```properties
# OpenAI API Key (OBLIGATOIRE)
SPRING_AI_OPENAI_API_KEY=sk-your-openai-api-key

# Google OAuth2 Credentials (OBLIGATOIRE)
GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-client-secret

# Database MySQL
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_DATABASE=baraka
MYSQL_USER=baraka_user
MYSQL_PASSWORD=baraka_pass
```

### 3. Démarrage avec Docker (Recommandé)

```bash
# Build et démarrage de tous les services
docker-compose up -d

# Vérifier les logs
docker-compose logs -f app

# Accéder à l'application
http://localhost
```

### 4. Démarrage Manuel

```bash
# Créer la base de données MySQL
mysql -u root -p
CREATE DATABASE baraka;
EXIT;

# Configurer les variables d'environnement
export SPRING_AI_OPENAI_API_KEY=sk-your-key
export GOOGLE_CLIENT_ID=your-client-id
export GOOGLE_CLIENT_SECRET=your-secret

# Build et démarrage
mvn clean package -DskipTests
java -jar target/baraka-0.0.1-SNAPSHOT.jar
```

L'application sera accessible sur : http://localhost:8080

## 🚀 Utilisation

### Comptes de Test

Après le premier démarrage, Liquibase crée automatiquement des utilisateurs de test :

| Rôle | Username | Password | Email |
|------|----------|----------|-------|
| CLIENT | client1 | password123 | client1@albaraka.com |
| AGENT | agent1 | password123 | agent1@albaraka.com |
| ADMIN | admin | admin123 | admin@albaraka.com |

### Flux de Travail Typique

#### 1. Client - Créer une Opération
1. Se connecter avec `client1` / `password123`
2. Aller sur "Nouvelle Opération"
3. Sélectionner type (DEPOSIT/WITHDRAWAL/TRANSFER)
4. Entrer montant et description
5. Upload documents justificatifs (si > 10 000 DH)
6. Soumettre

#### 2. IA - Validation Automatique
- Si montant ≤ 10 000 DH → Auto-approuvé
- Si montant > 10 000 DH → Analyse IA du document
  - **APPROVE** (confiance > 80%) → Opération approuvée
  - **REJECT** (confiance < 50%) → Opération rejetée
  - **NEED_HUMAN_REVIEW** (50-80%) → Envoi à un agent

#### 3. Agent - Validation Manuelle
1. Se connecter avec `agent1` / `password123`
2. Consulter "Opérations en Attente"
3. Examiner les détails et documents
4. Voir la recommandation de l'IA (score de confiance)
5. Approuver ou rejeter avec justification

#### 4. Admin - Gestion
1. Se connecter avec `admin` / `admin123`
2. Gérer les utilisateurs
3. Consulter statistiques IA
4. Activer/Désactiver comptes

## 🏗️ Architecture

### Structure du Projet
```
Al-Baraka-Digital/
├── src/main/java/com/Elbaraka/baraka/
│   ├── config/              # Configuration (Security, OAuth2)
│   ├── controller/          # REST Controllers
│   ├── dto/                 # Data Transfer Objects
│   ├── entity/              # JPA Entities
│   ├── enums/               # Enumerations
│   ├── exception/           # Custom Exceptions
│   ├── repository/          # JPA Repositories
│   ├── security/            # JWT Filters
│   ├── service/             # Business Logic
│   └── util/                # Utilities
├── src/main/resources/
│   ├── db/changelog/        # Liquibase Migrations
│   ├── templates/           # Thymeleaf Templates
│   │   ├── client/          # Client Dashboards
│   │   ├── agent/           # Agent Dashboards
│   │   └── admin/           # Admin Dashboards
│   └── application.properties
├── .github/workflows/       # CI/CD Pipelines
├── nginx/                   # Nginx Configuration
├── Dockerfile               # Multi-stage Docker Build
└── docker-compose.yml       # Orchestration
```

### Diagramme de Flux IA
```
[Client Upload Document] 
       ↓
[Tika Extract Text from PDF/Image]
       ↓
[OpenAI GPT-4o-mini Analysis]
       ↓
[Parse AI Response: Decision + Confidence]
       ↓
    ┌──────┴──────┐
    ↓             ↓
[APPROVE]    [NEED_HUMAN_REVIEW] → [Agent Review]
    ↓
[Update Balance]
```

## 🧪 Tests

```bash
# Tests unitaires
mvn test

# Tests avec couverture
mvn clean verify jacoco:report

# Rapport dans target/site/jacoco/index.html
```

## 🔒 Sécurité

### Mesures Implémentées
- ✅ **JWT Tokens** : Expiration 24h, secret sécurisé
- ✅ **OAuth2** : Google authentication flow
- ✅ **Remember-Me** : Tokens persistants en BDD
- ✅ **BCrypt** : Hachage password avec salt
- ✅ **RBAC** : Permissions basées sur rôles
- ✅ **HTTPS** : Support SSL/TLS via Nginx
- ✅ **Docker Non-Root** : Utilisateur `spring:spring`
- ✅ **Environment Variables** : Secrets dans .env

### Audit OWASP
```bash
# Scan vulnérabilités
docker run --rm -v $(pwd):/src owasp/dependency-check \
  --scan /src --format HTML --out /src/reports
```

## 📊 CI/CD Pipelines

### Workflows GitHub Actions
1. **Build** : Maven build + tests unitaires
2. **Docker** : Multi-arch image (amd64, arm64)
3. **Deploy** : SSH deployment to production
4. **Code Quality** : SonarCloud + OWASP scans

### Secrets Requis (GitHub)
- `SPRING_AI_OPENAI_API_KEY`
- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`
- `MYSQL_ROOT_PASSWORD`
- `MYSQL_PASSWORD`
- `SSH_PRIVATE_KEY` (pour déploiement)
- `SERVER_HOST` & `SERVER_USER`

## 📝 API Endpoints

### Authentification
- `POST /auth/register` - Inscription
- `POST /auth/login` - Connexion JWT
- `GET /oauth2/authorization/google` - OAuth2 Google

### Opérations (CLIENT)
- `POST /api/operations` - Créer opération
- `GET /api/operations` - Historique
- `GET /api/operations/{id}` - Détails

### Validation (AGENT)
- `GET /api/operations/pending` - Opérations en attente
- `POST /api/operations/{id}/approve` - Approuver
- `POST /api/operations/{id}/reject` - Rejeter

### IA Validation (AGENT/ADMIN)
- `GET /api/ai/validation/{operationId}` - Résultat IA
- `GET /api/ai/statistics` - Stats validation IA

### Administration (ADMIN)
- `GET /api/admin/users` - Liste utilisateurs
- `PUT /api/admin/users/{id}/toggle` - Activer/Désactiver

## 🌍 Déploiement Production

### Infrastructure Recommandée
- **Server** : Ubuntu 22.04 LTS (2 vCPU, 4GB RAM)
- **Docker** : Version 24+
- **Nginx** : Reverse proxy avec SSL
- **MySQL** : 8.0 avec volumes persistants

### Étapes Déploiement
1. Cloner le repository sur le serveur
2. Configurer `.env` avec secrets production
3. Obtenir certificat SSL (Let's Encrypt)
4. Démarrer avec `docker-compose up -d`
5. Configurer firewall (ports 80, 443)

### Monitoring
- **Healthcheck** : `/actuator/health`
- **Logs** : `docker-compose logs -f`
- **Métriques** : Spring Boot Actuator

## 🤝 Contribution

Les contributions sont les bienvenues ! Pour contribuer :

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 📄 Licence

Ce projet est sous licence MIT. Voir [LICENSE](LICENSE) pour plus de détails.

## 👥 Auteurs

- **Abdelmoudiri** - *Développeur Principal* - [GitHub](https://github.com/Abdelmoudiri)

## 🙏 Remerciements

- Spring Framework Team
- OpenAI pour GPT-4o-mini
- Apache Tika Community
- Bootstrap Team

---

**Al Baraka Digital** - Banque Digitale Intelligente 🚀
