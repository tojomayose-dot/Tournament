import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
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
      <div class="password-field">
        <input id="password" name="password" [type]="showPassword() ? 'text' : 'password'" [(ngModel)]="password" autocomplete="current-password" required [disabled]="busy()">
        <button type="button" class="secondary" aria-controls="password" [attr.aria-pressed]="showPassword()" [disabled]="busy()" (click)="showPassword.set(!showPassword())">{{ showPassword() ? 'Masquer' : 'Afficher' }}</button>
      </div>
      @if (error()) { <p role="alert" class="error">{{ error() }}</p> }
      <button [disabled]="form.invalid || busy()">{{ busy() ? 'Connexion…' : 'Se connecter' }}</button>
    </form>
  </main>`,
  styles: `
    .password-field { display: flex; gap: .5rem; align-items: stretch; }
    .password-field input { min-width: 0; flex: 1; }
    .password-field button { width: auto; margin: 0; flex-shrink: 0; }
  `
})
export class Login {
  private auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  username = '';
  password = '';
  showPassword = signal(false);
  busy = signal(false);
  error = signal('');
  async submit() {
    if (this.busy() || !this.username || !this.password) return;
    this.busy.set(true);
    this.error.set('');
    try {
      await this.auth.login(this.username, this.password);
      this.password = '';
      this.showPassword.set(false);
      const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
      const destination = returnUrl && /^\/(dashboard|tournaments)(?:[/?#]|$)/.test(returnUrl)
        ? returnUrl : '/dashboard';
      await this.router.navigateByUrl(destination);
    } catch (error) {
      this.error.set(error instanceof HttpErrorResponse && error.status === 401
        ? 'Identifiants incorrects ou compte désactivé.'
        : error instanceof HttpErrorResponse && error.status === 0
          ? 'Serveur injoignable. Vérifiez votre connexion et que le serveur est démarré.'
          : 'Connexion impossible. Réessayez dans un instant.');
    } finally { this.busy.set(false); }
  }
}
