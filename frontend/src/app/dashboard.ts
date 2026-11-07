import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  template: `<main class="card">
    <span class="badge">TournamentApp</span>
    <h1>Bienvenue, {{ auth.user()?.username }}</h1>
    <p>Rôle : {{ auth.user()?.role }}</p>
    <p>Votre espace est prêt. La gestion des tournois sera intégrée à la prochaine étape.</p>
    @if (error()) { <p role="alert" class="error">{{ error() }}</p> }
    <button (click)="logout()" [disabled]="busy()">Se déconnecter</button>
  </main>`
})
export class Dashboard {
  readonly auth = inject(AuthService);
  private router = inject(Router);
  busy = signal(false);
  error = signal('');
  async logout() {
    this.busy.set(true);
    this.error.set('');
    try { await this.auth.logout(); await this.router.navigateByUrl('/login'); }
    catch { this.error.set('Déconnexion impossible. Réessayez.'); }
    finally { this.busy.set(false); }
  }
}
