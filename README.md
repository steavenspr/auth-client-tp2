# Auth Client - TP2 a TP5

[![CI Maven + SonarCloud](https://github.com/steavenspr/auth-client/actions/workflows/ci-sonar.yml/badge.svg)](https://github.com/steavenspr/auth-client/actions/workflows/ci-sonar.yml)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=steavenspr_auth-client-tp2&metric=alert_status)](https://sonarcloud.io/project/overview?id=steavenspr_auth-client-tp2)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=steavenspr_auth-client-tp2&metric=coverage)](https://sonarcloud.io/project/overview?id=steavenspr_auth-client-tp2)
![Java](https://img.shields.io/badge/Java-17-blue)
![JavaFX](https://img.shields.io/badge/JavaFX-21-green)
![Maven](https://img.shields.io/badge/Maven-3.9+-orange)

Client desktop JavaFX pour le backend `auth-server`.
Le projet couvre l'evolution pedagogique jusqu'au TP5: inscription, authentification forte HMAC,
affichage de preuve de connexion (token + metadonnees), consultation du profil, et changement de mot de passe.

---

## Table des matieres
- [Stack technique](#stack-technique)
- [Prerequis](#prerequis)
- [Installation](#installation)
- [Lancement](#lancement)
- [Fonctionnalites](#fonctionnalites)
- [Flux d'authentification HMAC (client)](#flux-dauthentification-hmac-client)
- [Endpoints backend utilises](#endpoints-backend-utilises)
- [Architecture](#architecture)
- [Tests](#tests)
- [CI/CD et SonarCloud](#cicd-et-sonarcloud)
- [Compte de test](#compte-de-test)
- [Limites pedagogiques](#limites-pedagogiques)

---

## Stack technique

- Java 17
- JavaFX 21
- Maven
- HTTP Client Java (`java.net.http`)
- GitHub Actions (CI)
- SonarCloud (qualite de code)

---

## Prerequis

- Java 17
- Maven 3.9+
- Backend `auth-server` disponible sur `http://localhost:8080`

---

## Installation

```bash
git clone https://github.com/steavenspr/auth-client.git
cd auth-client
```

---

## Lancement

1. D'abord lancer le backend `auth-server`.

Exemple (backend avec Docker Compose):

```powershell
Set-Location "C:\Users\steav\IdeaProjects\auth-server"
docker compose up -d
```

2. Lancer le client JavaFX:

```powershell
Set-Location "C:\Users\steav\IdeaProjects\auth-client"
.\mvnw.cmd javafx:run
```

Alternative IDE: lancer `Launcher.java`.

---

## Fonctionnalites

- Inscription utilisateur (email + mot de passe)
- Connexion forte HMAC (`email:nonce:timestamp`)
- Affichage des preuves de connexion dans le profil:
  - `accessToken`
  - `expiresAt`
  - `email`
  - `nonce`
  - `timestamp`
- Consultation du profil (`/api/me`)
- Changement de mot de passe (`/api/auth/change-password`)
- Navigation entre vues: login, register, profil, changement de mot de passe
- Deconnexion vers l'ecran de login

---

## Flux d'authentification HMAC (client)

1. L'utilisateur saisit son email et son mot de passe.
2. Le client genere:
   - un nonce UUID
   - un timestamp en secondes
3. Le client construit `email:nonce:timestamp`.
4. Le client calcule `HMAC_SHA256(message, key=password)`.
5. Le client envoie `POST /api/auth/login` avec:

```json
{
  "email": "user@example.com",
  "nonce": "123e4567-e89b-12d3-a456-426614174000",
  "timestamp": 1711300000,
  "hmac": "..."
}
```

Le mot de passe n'est jamais envoye au backend pendant la connexion.

---

## Endpoints backend utilises

| Methode | Endpoint | Usage cote client |
|---------|----------|-------------------|
| POST | `/api/auth/register` | Inscription |
| POST | `/api/auth/login` | Connexion HMAC |
| GET | `/api/me` | Chargement du profil |
| PUT | `/api/auth/change-password` | Changement de mot de passe |

---

## Architecture

```text
src/main/java/com/example/autclient/
├── controllers/
│   ├── LoginController.java
│   ├── RegisterController.java
│   ├── ProfileController.java
│   └── ChangePasswordController.java
├── services/
│   ├── AuthService.java
│   ├── AuthSession.java
│   └── AuthServiceException.java
├── MainApp.java
└── Launcher.java

src/main/resources/com/example/autclient/
├── views/
│   ├── login-view.fxml
│   ├── register-view.fxml
│   ├── profile-view.fxml
│   └── change-password-view.fxml
└── styles.css
```

---

## Tests

Executer les tests:

```powershell
Set-Location "C:\Users\steav\IdeaProjects\auth-client"
.\mvnw.cmd test
```

Etat actuel verifie localement:
- 5 tests JUnit passent
  - `LoginControllerTest`: 2 tests (HMAC)
  - `AuthServiceTest`: 3 tests (parsing login/session)

---

## CI/CD et SonarCloud

- Workflow GitHub Actions: `.github/workflows/ci-sonar.yml`
- Declenchement: `push` et `pull_request` sur `main`
- Pipeline:
  - checkout
  - setup JDK 17
  - cache Maven
  - `mvn -B clean verify`
  - scan SonarCloud

Configuration Sonar: `sonar-project.properties`

---

## Compte de test

- Email: `toto@example.com`
- Password: `pwd1234`

---

## Limites pedagogiques

Ce projet est pedagogique. Le protocole est concu pour apprendre la preuve de connaissance
du mot de passe sans transmission du secret lors du login. En production, on privilegierait
des protocoles standardises (ex: SRP, OPAQUE) et une approche complete de gestion de session.

