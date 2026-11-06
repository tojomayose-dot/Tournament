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

Cette première étape crée uniquement le frontend. Les pages métier, les appels HTTP
et l’authentification ne sont pas encore intégrés. L’application Angular peut démarrer
sans le backend. Le backend Spring Boot et l’interface Thymeleaf restent disponibles
comme auparavant ; aucun proxy API n’est encore configuré.
