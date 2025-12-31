# Test des Endpoints - Al Baraka Digital

## 🔐 Authentification

### 1. S'inscrire (Register)
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "client1@example.com",
    "password": "password123",
    "fullName": "Client Test 1"
  }'
```

### 2. Se connecter (Login)
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "client1@example.com",
    "password": "password123"
  }'
```

**Récupérer le TOKEN depuis la réponse et l'utiliser dans les requêtes suivantes**

---

## 👤 Profil Client

### 3. Consulter son profil
```bash
curl -X GET http://localhost:8080/api/client/profile \
  -H "Authorization: Bearer VOTRE_TOKEN_ICI"
```

**Exemple de réponse :**
```json
{
  "userId": 1,
  "email": "client1@example.com",
  "fullName": "Client Test 1",
  "accountNumber": "ACC-1234567890",
  "balance": 0.00,
  "active": true
}
```

---

## 💰 Opérations Bancaires

### 4. Dépôt (≤ 10 000 DH - Auto-validé)
```bash
curl -X POST http://localhost:8080/api/client/operations/deposit \
  -H "Authorization: Bearer VOTRE_TOKEN_ICI" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 5000.00
  }'
```

**Réponse attendue :**
```json
{
  "id": 1,
  "type": "DEPOSIT",
  "amount": 5000.00,
  "status": "COMPLETED",
  "createdAt": "2025-12-25T21:30:00",
  "executedAt": "2025-12-25T21:30:00",
  "sourceAccountNumber": "ACC-1234567890",
  "destinationAccountNumber": null
}
```

### 5. Dépôt (> 10 000 DH - En attente de validation)
```bash
curl -X POST http://localhost:8080/api/client/operations/deposit \
  -H "Authorization: Bearer VOTRE_TOKEN_ICI" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 15000.00
  }'
```

**Réponse attendue :**
```json
{
  "id": 2,
  "type": "DEPOSIT",
  "amount": 15000.00,
  "status": "PENDING",
  "createdAt": "2025-12-25T21:35:00",
  "executedAt": null,
  "sourceAccountNumber": "ACC-1234567890",
  "destinationAccountNumber": null
}
```

### 6. Retrait (≤ 10 000 DH - Auto-validé)
```bash
curl -X POST http://localhost:8080/api/client/operations/withdrawal \
  -H "Authorization: Bearer VOTRE_TOKEN_ICI" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 1000.00
  }'
```

### 7. Retrait avec solde insuffisant (Erreur 400)
```bash
curl -X POST http://localhost:8080/api/client/operations/withdrawal \
  -H "Authorization: Bearer VOTRE_TOKEN_ICI" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 99999.00
  }'
```

### 8. Virement vers un autre compte
**D'abord, créer un 2ème utilisateur pour avoir un compte destination :**

```bash
# Inscription client 2
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "client2@example.com",
    "password": "password123",
    "fullName": "Client Test 2"
  }'
```

**Récupérer le numéro de compte du client 2 (dans la réponse du register)**

**Puis effectuer un virement :**
```bash
curl -X POST http://localhost:8080/api/client/operations/transfer \
  -H "Authorization: Bearer TOKEN_CLIENT1" \
  -H "Content-Type: application/json" \
  -d '{
    "destinationAccountNumber": "NUMERO_COMPTE_CLIENT2",
    "amount": 500.00
  }'
```

**Réponse attendue :**
```json
{
  "id": 3,
  "type": "TRANSFER",
  "amount": 500.00,
  "status": "COMPLETED",
  "createdAt": "2025-12-25T21:40:00",
  "executedAt": "2025-12-25T21:40:00",
  "sourceAccountNumber": "ACC-1234567890",
  "destinationAccountNumber": "ACC-0987654321"
}
```

### 9. Consulter l'historique des opérations
```bash
curl -X GET http://localhost:8080/api/client/operations \
  -H "Authorization: Bearer VOTRE_TOKEN_ICI"
```

**Réponse attendue (liste) :**
```json
[
  {
    "id": 3,
    "type": "TRANSFER",
    "amount": 500.00,
    "status": "COMPLETED",
    "createdAt": "2025-12-25T21:40:00",
    "executedAt": "2025-12-25T21:40:00",
    "sourceAccountNumber": "ACC-1234567890",
    "destinationAccountNumber": "ACC-0987654321"
  },
  {
    "id": 2,
    "type": "DEPOSIT",
    "amount": 15000.00,
    "status": "PENDING",
    "createdAt": "2025-12-25T21:35:00",
    "executedAt": null,
    "sourceAccountNumber": "ACC-1234567890",
    "destinationAccountNumber": null
  },
  {
    "id": 1,
    "type": "DEPOSIT",
    "amount": 5000.00,
    "status": "COMPLETED",
    "createdAt": "2025-12-25T21:30:00",
    "executedAt": "2025-12-25T21:30:00",
    "sourceAccountNumber": "ACC-1234567890",
    "destinationAccountNumber": null
  }
]
```

---

## ✅ Règles de Validation Automatique

| Montant | Statut | Solde mis à jour |
|---------|--------|------------------|
| ≤ 10 000 DH | **COMPLETED** (immédiat) | ✅ Oui |
| > 10 000 DH | **PENDING** (attente validation agent) | ❌ Non |

---

## 🔴 Gestion des Erreurs

### Erreur 400 - Solde insuffisant
```bash
{
  "message": "Solde insuffisant"
}
```

### Erreur 400 - Compte destination inexistant
```bash
{
  "message": "Compte destination non trouvé"
}
```

### Erreur 401 - Non authentifié
```bash
{
  "message": "Unauthorized"
}
```

### Erreur 403 - Accès interdit
```bash
{
  "message": "Forbidden"
}
```

---

## 📊 Scénario de Test Complet

1. **Créer client 1** → `client1@example.com`
2. **Se connecter** → Obtenir `TOKEN1`
3. **Dépôt 5000 DH** → Solde = 5000 DH (COMPLETED)
4. **Créer client 2** → `client2@example.com`
5. **Virement 2000 DH** (client1 → client2) → Solde client1 = 3000 DH, client2 = 2000 DH
6. **Retrait 1000 DH** → Solde client1 = 2000 DH
7. **Dépôt 15000 DH** → Solde inchangé (PENDING)
8. **Consulter historique** → Voir toutes les opérations

---

## 🎯 Prochaines Étapes

### Phase 2 - Workflow Agent ✅ TERMINÉ

---

## 👨‍💼 Agent Bancaire

### 10. Consulter les opérations en attente (PENDING)
```bash
# D'abord créer un agent via la base de données ou endpoint admin
# Puis se connecter en tant qu'agent

curl -X GET http://localhost:8080/api/agent/operations/pending \
  -H "Authorization: Bearer TOKEN_AGENT"
```

**Réponse attendue :**
```json
[
  {
    "id": 2,
    "type": "DEPOSIT",
    "amount": 15000.00,
    "status": "PENDING",
    "createdAt": "2025-12-25T21:35:00",
    "executedAt": null,
    "sourceAccountNumber": "ACC-1234567890",
    "destinationAccountNumber": null
  }
]
```

### 11. Approuver une opération
```bash
curl -X PUT http://localhost:8080/api/agent/operations/2/approve \
  -H "Authorization: Bearer TOKEN_AGENT"
```

**Réponse attendue :**
```json
{
  "id": 2,
  "type": "DEPOSIT",
  "amount": 15000.00,
  "status": "APPROVED",
  "createdAt": "2025-12-25T21:35:00",
  "executedAt": "2025-12-25T22:00:00",
  "sourceAccountNumber": "ACC-1234567890",
  "destinationAccountNumber": null
}
```

**Note** : Le solde du compte sera mis à jour après l'approbation.

### 12. Rejeter une opération
```bash
curl -X PUT http://localhost:8080/api/agent/operations/3/reject \
  -H "Authorization: Bearer TOKEN_AGENT"
```

**Réponse attendue :**
```json
{
  "id": 3,
  "type": "WITHDRAWAL",
  "amount": 12000.00,
  "status": "REJECTED",
  "createdAt": "2025-12-25T21:40:00",
  "executedAt": null,
  "sourceAccountNumber": "ACC-1234567890",
  "destinationAccountNumber": null
}
```

**Note** : Le solde ne sera PAS modifié pour les opérations rejetées.

### 13. Consulter toutes les opérations (Agent)
```bash
curl -X GET http://localhost:8080/api/agent/operations \
  -H "Authorization: Bearer TOKEN_AGENT"
```

---

## 🔧 Administration

### 14. Lister tous les utilisateurs
```bash
curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer TOKEN_ADMIN"
```

### 15. Activer un compte utilisateur
```bash
curl -X PUT http://localhost:8080/api/admin/users/1/activate \
  -H "Authorization: Bearer TOKEN_ADMIN"
```

### 16. Désactiver un compte utilisateur
```bash
curl -X PUT http://localhost:8080/api/admin/users/1/deactivate \
  -H "Authorization: Bearer TOKEN_ADMIN"
```

### 17. Consulter toutes les opérations (Admin)
```bash
curl -X GET http://localhost:8080/api/admin/operations \
  -H "Authorization: Bearer TOKEN_ADMIN"
```

---

## 📋 Workflows Complets

### Workflow 1 : Dépôt > 10 000 DH avec validation

1. **Client crée un dépôt de 15 000 DH**
   ```bash
   POST /api/client/operations/deposit
   ```
   → Statut: `PENDING`, Solde inchangé

2. **Agent consulte les opérations en attente**
   ```bash
   GET /api/agent/operations/pending
   ```

3. **Agent approuve l'opération**
   ```bash
   PUT /api/agent/operations/{id}/approve
   ```
   → Statut: `APPROVED`, Solde +15 000 DH

### Workflow 2 : Virement > 10 000 DH avec rejet

1. **Client crée un virement de 20 000 DH**
   ```bash
   POST /api/client/operations/transfer
   ```
   → Statut: `PENDING`, Soldes inchangés

2. **Agent rejette l'opération**
   ```bash
   PUT /api/agent/operations/{id}/reject
   ```
   → Statut: `REJECTED`, Soldes inchangés

---

## 📎 Gestion des Documents

### 17. Upload d'un justificatif (pour opération > 10 000 DH)
```bash
# Créer d'abord un fichier test
echo "Justificatif bancaire" > justificatif.pdf

# Upload avec curl
curl -X POST http://localhost:8080/api/client/operations/2/documents \
  -H "Authorization: Bearer VOTRE_TOKEN_CLIENT" \
  -F "file=@justificatif.pdf"
```

**Réponse attendue :**
```json
{
  "id": 1,
  "fileName": "justificatif.pdf",
  "fileType": "application/pdf",
  "uploadedAt": "2025-12-25T21:45:00",
  "message": "Document téléchargé avec succès"
}
```

**Types de fichiers acceptés :** PDF, JPG, PNG  
**Taille maximale :** 10 MB

### 18. Lister les documents d'une opération
```bash
curl -X GET http://localhost:8080/api/operations/2/documents \
  -H "Authorization: Bearer VOTRE_TOKEN_ICI"
```

**Réponse attendue :**
```json
[
  {
    "id": 1,
    "fileName": "justificatif.pdf",
    "fileType": "application/pdf",
    "uploadedAt": "2025-12-25T21:45:00"
  }
]
```

### 19. Télécharger un document
```bash
curl -X GET http://localhost:8080/api/documents/1/download \
  -H "Authorization: Bearer VOTRE_TOKEN_ICI" \
  -o document_telecharge.pdf
```

Le fichier sera téléchargé dans le répertoire courant.

### 20. Supprimer un document
```bash
curl -X DELETE http://localhost:8080/api/client/documents/1 \
  -H "Authorization: Bearer VOTRE_TOKEN_CLIENT"
```

**Réponse attendue :**
```json
{
  "message": "Document supprimé avec succès"
}
```

### ⚠️ Règle importante - Validation avec documents
Lorsqu'un agent tente d'approuver une opération > 10 000 DH **sans document** :

```bash
curl -X PUT http://localhost:8080/api/agent/operations/2/approve \
  -H "Authorization: Bearer VOTRE_TOKEN_AGENT"
```

**Erreur attendue :**
```json
{
  "error": "Un justificatif est requis pour les opérations supérieures à 10 000 DH"
}
```

### 📋 Workflow complet avec documents

1. **Client fait un dépôt > 10 000 DH**
   ```bash
   POST /api/client/operations/deposit (amount: 15000)
   ```
   → Statut: `PENDING`

2. **Client uploade un justificatif**
   ```bash
   POST /api/client/operations/{id}/documents
   ```
   → Document enregistré

3. **Agent vérifie et approuve**
   ```bash
   PUT /api/agent/operations/{id}/approve
   ```
   → Statut: `APPROVED`, Solde mis à jour

---

## 🚀 Récapitulatif des Endpoints

| Méthode | Endpoint | Rôle | Description |
|---------|----------|------|-------------|
| POST | `/auth/register` | Public | Inscription |
| POST | `/auth/login` | Public | Connexion |
| GET | `/api/client/profile` | CLIENT | Profil |
| POST | `/api/client/operations/deposit` | CLIENT | Dépôt |
| POST | `/api/client/operations/withdrawal` | CLIENT | Retrait |
| POST | `/api/client/operations/transfer` | CLIENT | Virement |
| GET | `/api/client/operations` | CLIENT | Historique |
| POST | `/api/client/operations/{id}/documents` | CLIENT | Upload document |
| DELETE | `/api/client/documents/{id}` | CLIENT | Supprimer document |
| GET | `/api/operations/{id}/documents` | ALL | Liste documents |
| GET | `/api/documents/{id}/download` | ALL | Télécharger document |
| GET | `/api/agent/operations/pending` | AGENT | Liste PENDING |
| PUT | `/api/agent/operations/{id}/approve` | AGENT | Approuver |
| PUT | `/api/agent/operations/{id}/reject` | AGENT | Rejeter |
| GET | `/api/agent/operations` | AGENT | Toutes les ops |
| GET | `/api/admin/users` | ADMIN | Liste users |
| PUT | `/api/admin/users/{id}/activate` | ADMIN | Activer |
| PUT | `/api/admin/users/{id}/deactivate` | ADMIN | Désactiver |
| GET | `/api/admin/operations` | ADMIN | Toutes les ops |

---

## 🎯 Prochaines Étapes

### Phase 4 - Tests & Documentation (RESTE À FAIRE)
- Tests unitaires et d'intégration
- Documentation Swagger/OpenAPI
- Docker Compose pour déploiement
