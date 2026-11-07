# Frontend TournamentApp

Application Angular 22 avec composants standalone, routage initialisé et styles CSS.

## Prérequis

Node.js 22.22.3 ou une version compatible avec Angular 22, et npm.

## Installation et démarrage

Depuis la racine du dépôt :

```bash
cd frontend
npm ci
npm start
```

Ouvrir http://localhost:4200. Le serveur recharge la page lors des modifications.
Les dépendances étant déjà installées à la création, `npm start` suffit pour le premier lancement.

## Compilation de production

```bash
npm run build
```

Les fichiers à déployer sont générés dans `dist/tournament-frontend/browser/`.

## Organisation

- `src/app/app.*` : composant racine et page de démarrage.
- `src/app/app.routes.ts` : routes à compléter lors de la migration des pages.
- `src/app/app.config.ts` : configuration de l’application.
- `src/styles.css` : styles globaux.
- `public/` : fichiers statiques.

## Connexion au backend

Démarrer Spring Boot sur le port 8080 avec MySQL configuré, puis lancer `npm start`.
Le proxy de développement transmet `/api/**` au backend. Se connecter sur
http://localhost:4200/login avec un compte existant créé par l’administrateur.

La connexion utilise une session serveur (cookie HttpOnly), sans stockage du mot de
passe dans le navigateur. Le rôle est affiché sur la page d’accueil protégée et la
session est restaurée au rechargement. Les mutations API nécessitent désormais un
jeton CSRF : appeler `GET /api/auth/csrf`, puis transmettre `token` dans l’en-tête
indiqué par `headerName`. Le service de connexion le fait pour login et logout.

Les pages métier restent à intégrer. En production, servir le frontend et `/api`
sous la même origine et configurer le serveur pour renvoyer `index.html` sur les
routes Angular. Le proxy fourni s’applique uniquement au développement.
