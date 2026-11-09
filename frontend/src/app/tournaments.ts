import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { catchError, of, startWith, Subject, switchMap } from 'rxjs';
import { AuthService } from './auth.service';
import { TournamentPage, TournamentService, TournamentStatus, tournamentStatuses } from './tournament.service';

@Component({
  imports: [DatePipe, FormsModule],
  templateUrl: './tournaments.html',
  styleUrl: './tournaments.css',
})
export class Tournaments {
  private readonly tournaments = inject(TournamentService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly requests = new Subject<void>();
  readonly statuses = tournamentStatuses;
  readonly result = signal<TournamentPage | null>(null);
  readonly loading = signal(true);
  readonly error = signal('');
  sport = '';
  status: TournamentStatus | '' = '';
  private appliedSport = '';
  private appliedStatus: TournamentStatus | '' = '';
  private page = 0;

  constructor() {
    this.requests.pipe(
      startWith(undefined),
      switchMap(() => {
        this.loading.set(true);
        this.error.set('');
        this.result.set(null);
        return this.tournaments.list(this.page, this.appliedSport, this.appliedStatus).pipe(
          catchError((error: unknown) => {
            if (error instanceof HttpErrorResponse && error.status === 401) {
              this.auth.user.set(null);
              void this.router.navigateByUrl('/login');
            } else {
              this.error.set('Impossible de charger les tournois. Réessayez dans un instant.');
            }
            return of(null);
          }),
        );
      }),
      takeUntilDestroyed(),
    ).subscribe(result => {
      this.result.set(result);
      this.loading.set(false);
    });
  }

  search() {
    this.appliedSport = this.sport.trim();
    this.appliedStatus = this.status;
    this.page = 0;
    this.reload();
  }

  reset() {
    this.sport = '';
    this.status = '';
    this.search();
  }

  reload() { this.requests.next(); }

  goToPage(page: number) {
    const result = this.result();
    if (this.loading() || !result || page < 0 || page >= result.totalPages) return;
    this.page = page;
    this.reload();
  }

  statusLabel(status: TournamentStatus) {
    return this.statuses.find(item => item.value === status)?.label ?? status;
  }
}
