import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

export type TournamentStatus = 'BROUILLON' | 'INSCRIPTION' | 'PROGRAMME' | 'EN_COURS' | 'TERMINE' | 'ANNULE';

export const tournamentStatuses: { value: TournamentStatus; label: string }[] = [
  { value: 'BROUILLON', label: 'Brouillon' },
  { value: 'INSCRIPTION', label: 'Inscriptions ouvertes' },
  { value: 'PROGRAMME', label: 'Programmé' },
  { value: 'EN_COURS', label: 'En cours' },
  { value: 'TERMINE', label: 'Terminé' },
  { value: 'ANNULE', label: 'Annulé' },
];

export interface Tournament {
  id: number;
  name: string;
  sport: string;
  description: string | null;
  startDate: string | null;
  endDate: string | null;
  location: string | null;
  maxTeams: number;
  type: 'CHAMPIONNAT' | 'ELIMINATION_DIRECTE';
  status: TournamentStatus;
}

export interface TournamentPage {
  content: Tournament[];
  totalElements: number;
  totalPages: number;
  number: number;
}

@Injectable({ providedIn: 'root' })
export class TournamentService {
  private readonly http = inject(HttpClient);

  list(page: number, sport: string, status: TournamentStatus | '') {
    let params = new HttpParams()
      .set('page', page)
      .set('size', 10)
      .set('sort', 'startDate,desc')
      .append('sort', 'id,desc');
    if (sport.trim()) params = params.set('sport', sport.trim());
    if (status) params = params.set('status', status);
    return this.http.get<TournamentPage>('/api/tournaments', { params });
  }
}
