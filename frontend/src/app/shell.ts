import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  template: `
    <div class="workspace">
      <aside class="sidebar">
        <a class="brand" routerLink="/dashboard">Tournament<span>App</span></a>
        <p class="nav-heading">MON ESPACE</p>
        <nav aria-label="Navigation principale">
          <a routerLink="/dashboard" routerLinkActive="active" ariaCurrentWhenActive="page">Tableau de bord</a>
          <a routerLink="/tournaments" routerLinkActive="active" ariaCurrentWhenActive="page">Tournois</a>
        </nav>
        <div class="account">
          <strong>{{ auth.user()?.username }}</strong>
          <span>{{ auth.user()?.role }}</span>
          <button class="secondary" (click)="logout()" [disabled]="busy()">
            {{ busy() ? 'Déconnexion…' : 'Se déconnecter' }}
          </button>
          @if (error()) { <p role="alert" class="error">{{ error() }}</p> }
        </div>
      </aside>
      <main class="page"><router-outlet /></main>
    </div>
  `,
  styleUrl: './shell.css',
})
export class Shell {
  readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly busy = signal(false);
  readonly error = signal('');

  async logout() {
    if (this.busy()) return;
    this.busy.set(true);
    this.error.set('');
    try {
      await this.auth.logout();
      await this.router.navigateByUrl('/login');
    } catch {
      this.error.set('Déconnexion impossible. Réessayez.');
    } finally {
      this.busy.set(false);
    }
  }
}
