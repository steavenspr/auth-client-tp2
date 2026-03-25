# Auth Client - TP3 (Authentification forte)

## Description
Client JavaFX pour le serveur d'authentification TP3.
Permet de s'inscrire, se connecter et voir son profil
via une interface graphique qui communique avec le backend
par des requêtes HTTP sécurisées.

**Protocole d'authentification forte** :
- Le mot de passe n'est jamais transmis au serveur, ni en clair, ni haché.
- Le client prouve qu'il connaît le secret (mot de passe) en calculant un HMAC-SHA256 sur le message `email:nonce:timestamp` avec le mot de passe comme clé.
- Le client envoie un JSON `{ email, nonce, timestamp, hmac }` au backend.
- Le serveur vérifie la preuve, protège contre le rejeu (nonce), et limite la fenêtre temporelle (timestamp).

**Caractère pédagogique** :
- Le mot de passe est stocké chiffré de façon réversible côté serveur (via une Server Master Key, SMK).
- Ce protocole est pour l'apprentissage : en production, on utiliserait un hash non réversible et un protocole éprouvé (ex : SRP, OPAQUE).

Stack : Java 17, JavaFX 21, Maven

---

## Prérequis
- Java 17
- Maven 3.9
- Le backend `auth-server-tp3` lancé sur le port 8080

---

## Installation et lancement

1. Cloner le projet :
```bash
git clone https://github.com/steavenspr/auth-client.git
```

2. Lancer d'abord le backend (voir README du backend)

3. Lancer le client :
```bash
mvn javafx:run
```

Ou depuis IntelliJ : lancer la classe `Launcher.java`

---

## Fonctionnalités

- Inscription avec email et mot de passe
- Connexion forte (preuve de connaissance du mot de passe sans l'envoyer)
- Génération et gestion du nonce (anti-rejeu) et du timestamp (fenêtre de validité)
- Calcul HMAC côté client
- Récupération du token d'accès après authentification
- Affichage du profil après connexion
- Déconnexion et retour à l'écran de login
- Navigation entre les vues (login ↔ register)

---

## Protocole d'authentification (côté client)

1. L'utilisateur saisit son email et son mot de passe.
2. Le client génère un nonce (UUID) et un timestamp (epoch secondes).
3. Le message à signer est :
   ```
   email:nonce:timestamp
   ```
4. Le client calcule le HMAC-SHA256 de ce message avec le mot de passe comme clé.
5. Le client envoie la requête POST `/api/auth/login` avec le payload JSON :
   ```json
   {
     "email": "toto@example.com",
     "nonce": "123e4567-e89b-12d3-a456-426614174000",
     "timestamp": 1711300000,
     "hmac": "..."
   }
   ```
6. Le mot de passe n'est jamais transmis ni loggé.

---

## Architecture
```
controllers/
├── LoginController.java     → Gère la vue login et le protocole fort
├── RegisterController.java  → Gère la vue inscription
└── ProfileController.java   → Gère la vue profil

services/
└── AuthService.java         → Appels HTTP vers le backend

resources/views/
├── login-view.fxml
├── register-view.fxml
└── profile-view.fxml
```

---

## Compte de test
- Email : toto@example.com
- Password : pwd1234

---

## Avertissement sécurité
Ce projet est pédagogique. Le stockage du mot de passe chiffré de façon réversible n'est pas recommandé en production. Préférer un hash non réversible et un protocole d'authentification éprouvé.

