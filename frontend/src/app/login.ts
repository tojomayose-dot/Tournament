import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from './auth.service';

@Component({
  imports: [FormsModule],
  template: `<main class="card">
    <span class="badge">TournamentApp</span>
    <h1>Connexion</h1>
    <p>Connectez-vous pour gérer vos tournois.</p>
    <form (ngSubmit)="submit()" #form="ngForm">
      <label for="username">Nom d’utilisateur</label>
      <input id="username" name="username" [(ngModel)]="username" autocomplete="username" required [disabled]="busy()">
      <label for="password">Mot de passe</label>
      <input id="password" name="password" type="password" [(ngModel)]="password" autocomplete="current-password" required [disabled]="busy()">
      @if (error()) { <p role="alert" class="error">{{ error() }}</p> }
      <button [disabled]="form.invalid || busy()">{{ busy() ? 'Connexion…' : 'Se connecter' }}</button>
    </form>
  </main>`
})
export class Login {
  private auth = inject(AuthService);
  private router = inject(Router);
  username = '';
  password = '';
  busy = signal(false);
  error = signal('');
  async submit() {
    if (this.busy() || !this.username || !this.password) return;
    this.busy.set(true);
    this.error.set('');
    try {
      await this.auth.login(this.username, this.password);
      this.password = '';
      await this.router.navigateByUrl('/dashboard');
    } catch (error) {
      this.error.set(error instanceof HttpErrorResponse && error.status === 401
        ? 'Identifiants incorrects ou compte désactivé.' : 'Connexion impossible. Réessayez dans un instant.');
    } finally { this.busy.set(false); }
  }
}
