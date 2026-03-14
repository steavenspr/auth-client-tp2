# Auth Client - TP1

## Description
Client JavaFX pour le serveur d'authentification TP1.
Permet de s'inscrire, se connecter et voir son profil
via une interface graphique qui communique avec le backend
par des requêtes HTTP.

Stack : Java 17, JavaFX 21, Maven

---

## Prérequis
- Java 17
- Maven 3.9
- Le backend `auth-server-tp1` lancé sur le port 8080

---

## Installation et lancement

1. Cloner le projet :
```bash
git clone https://github.com/steavenspr/aut-client.git
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
- Connexion et récupération du token
- Affichage du profil après connexion
- Déconnexion et retour à l'écran de login
- Navigation entre les vues (login ↔ register)

---

## Architecture
```
controllers/
├── LoginController.java     → Gère la vue login
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