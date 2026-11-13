import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  imports: [RouterLink],
  template: `<section>
    <span class="badge">TournamentApp</span>
    <h1>Bienvenue, {{ auth.user()?.username }}</h1>
    <p>Rôle : {{ auth.roleLabel() }}</p>
    <p>Consultez les compétitions, filtrez-les par sport et suivez leur statut.</p>
    <a routerLink="/tournaments" class="button-link">Voir les tournois</a>
  </section>`
})
export class Dashboard {
  readonly auth = inject(AuthService);
}
