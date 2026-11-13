import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, combineLatest, of, startWith, Subject, switchMap } from 'rxjs';
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
  private readonly route = inject(ActivatedRoute);
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
    combineLatest([this.route.queryParamMap, this.requests.pipe(startWith(undefined))]).pipe(
      switchMap(([params]) => {
        this.sport = params.get('sport')?.trim() ?? '';
        const status = params.get('status');
        this.status = this.statuses.find(item => item.value === status)?.value ?? '';
        const rawPage = params.get('page') ?? '0';
        const page = Number(rawPage);
        this.page = /^\d+$/.test(rawPage) && Number.isSafeInteger(page) ? page : 0;
        this.appliedSport = this.sport;
        this.appliedStatus = this.status;
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
    this.navigate();
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
    this.navigate();
  }

  private navigate() {
    const queryParams = {
      sport: this.appliedSport || null,
      status: this.appliedStatus || null,
      page: this.page || null,
    };
    // A repeated search refreshes results even when the URL stays the same.
    const target = this.router.createUrlTree([], { relativeTo: this.route, queryParams });
    if (this.router.serializeUrl(target) === this.router.url) this.reload();
    else void this.router.navigateByUrl(target);
  }

  statusLabel(status: TournamentStatus) {
    return this.statuses.find(item => item.value === status)?.label ?? status;
  }
}
