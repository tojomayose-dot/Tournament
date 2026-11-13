import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router, RouterLink } from '@angular/router';
import { catchError, of, startWith, Subject, switchMap } from 'rxjs';
import { AuthService } from './auth.service';
import { TournamentPage, TournamentService, TournamentStatus, tournamentStatuses } from './tournament.service';

@Component({
  imports: [DatePipe, RouterLink],
  template: `<section>
    <span class="badge">TournamentApp</span>
    <h1>Bienvenue, {{ auth.user()?.username }}</h1>
    <p>Rôle : {{ auth.roleLabel() }}</p>
    <p>Consultez les compétitions, filtrez-les par sport et suivez leur statut.</p>
    <a routerLink="/tournaments" class="button-link">Voir les tournois</a>
  </section>
  <section class="overview" aria-labelledby="overview-title" [attr.aria-busy]="loading()">
    <h2 id="overview-title">Aperçu des tournois</h2>
    @if (loading()) {
      <p role="status">Chargement des tournois…</p>
    } @else if (error()) {
      <p class="error" role="alert">{{ error() }}</p>
      <button class="secondary retry" (click)="reload()">Réessayer</button>
    } @else if (result(); as data) {
      <p role="status">{{ data.totalElements }} tournoi{{ data.totalElements > 1 ? 's' : '' }} disponible{{ data.totalElements > 1 ? 's' : '' }}.</p>
      @if (data.content.length === 0) {
        <p>Aucun tournoi pour le moment.</p>
      } @else {
        <p>Jusqu’à cinq tournois, par date de début décroissante.</p>
        <ul>
          @for (tournament of data.content.slice(0, 5); track tournament.id) {
            <li>
              <a [routerLink]="['/tournaments', tournament.id]">{{ tournament.name }}</a>
              <span>{{ tournament.sport }} · {{ statusLabel(tournament.status) }}</span>
              <span>Début : {{ tournament.startDate ? (tournament.startDate | date:'dd/MM/yyyy') : 'À définir' }}</span>
            </li>
          }
        </ul>
      }
    }
  </section>`,
  styles: `
    .overview { margin-top: 2rem; padding: 1.5rem; background: white; border: 1px solid #e2e8f0; border-radius: 16px; }
    h2 { margin-top: 0; font-size: 1.2rem; }
    ul { list-style: none; padding: 0; margin-bottom: 0; }
    li { display: grid; gap: .4rem; padding: 1rem 0; border-top: 1px solid #e2e8f0; overflow-wrap: anywhere; }
    li a { color: #4338ca; font-weight: 600; }
    li span { color: #475569; font-size: .875rem; }
    .retry { width: auto; }
  `,
})
export class Dashboard {
  readonly auth = inject(AuthService);
  private readonly service = inject(TournamentService);
  private readonly router = inject(Router);
  private readonly requests = new Subject<void>();
  readonly result = signal<TournamentPage | null>(null);
  readonly loading = signal(true);
  readonly error = signal('');

  constructor() {
    this.requests.pipe(
      startWith(undefined),
      switchMap(() => {
        this.loading.set(true);
        this.error.set('');
        return this.service.list(0, '', '').pipe(catchError((error: unknown) => {
          if (error instanceof HttpErrorResponse && error.status === 401) {
            this.auth.user.set(null);
            void this.router.navigate(['/login'], {
              queryParams: { returnUrl: '/dashboard', reason: 'session-expired' },
            });
          } else {
            this.error.set('Impossible de charger les tournois. Réessayez dans un instant.');
          }
          return of(null);
        }));
      }),
      takeUntilDestroyed(),
    ).subscribe(result => {
      this.result.set(result);
      this.loading.set(false);
    });
  }

  reload() { this.requests.next(); }

  statusLabel(status: TournamentStatus) {
    return tournamentStatuses.find(item => item.value === status)?.label ?? status;
  }
}
