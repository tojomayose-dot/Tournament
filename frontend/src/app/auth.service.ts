import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

export interface User { id: number; username: string; role: 'ADMIN' | 'MANAGER' | 'OPERATOR' | 'USER' }
@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  readonly user = signal<User | null>(null);
  readonly roleLabel = computed(() => {
    const role = this.user()?.role;
    return role ? {
      ADMIN: 'Administrateur',
      MANAGER: 'Gestionnaire',
      OPERATOR: 'Opérateur',
      USER: 'Utilisateur',
    }[role] : '';
  });
  async restore(): Promise<boolean> {
    try {
      this.user.set(await firstValueFrom(this.http.get<User>('/api/auth/me')));
      return true;
    } catch (error) {
      this.user.set(null);
      throw error;
    }
  }
  private async headers() {
    const csrf = await firstValueFrom(this.http.get<{ token: string; headerName: string }>('/api/auth/csrf'));
    return { [csrf.headerName]: csrf.token };
  }
  async login(username: string, password: string) {
    const headers = await this.headers();
    this.user.set(await firstValueFrom(this.http.post<User>('/api/auth/login', { username, password }, { headers })));
  }
  async logout() {
    const headers = await this.headers();
    await firstValueFrom(this.http.post('/api/auth/logout', {}, { headers }));
    this.user.set(null);
  }
}
