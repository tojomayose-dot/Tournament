import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  imports: [RouterLink],
  template: `
    <main class="card" aria-labelledby="not-found-title">
      <span class="badge">Erreur 404</span>
      <h1 id="not-found-title">Page introuvable</h1>
      <p>Cette adresse ne correspond à aucune page. Vérifiez le lien ou poursuivez depuis le tableau de bord.</p>
      <nav aria-label="Retrouver une page">
        <a routerLink="/dashboard" class="button-link">Tableau de bord</a>
        <a routerLink="/tournaments" class="tournaments-link">Voir les tournois</a>
      </nav>
    </main>
  `,
  styles: `
    nav { display: flex; align-items: center; flex-wrap: wrap; gap: 1rem; }
    .button-link { margin-top: 0; }
    .tournaments-link { color: #4338ca; padding: .8rem 0; }
  `,
})
export class NotFound {}
