import { inject } from '@angular/core';
import { CanActivateFn, Router, Routes } from '@angular/router';
import { AuthService } from './auth.service';
import { Login } from './login';
import { Dashboard } from './dashboard';
import { Shell } from './shell';
import { Tournaments } from './tournaments';

const authenticated: CanActivateFn = async () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  try { await auth.restore(); return true; }
  catch { return router.createUrlTree(['/login']); }
};
export const routes: Routes = [
  { path: 'login', component: Login },
  {
    path: '', component: Shell, canActivate: [authenticated], canActivateChild: [authenticated],
    children: [
      { path: 'dashboard', component: Dashboard },
      { path: 'tournaments', component: Tournaments },
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
    ],
  },
  { path: '**', redirectTo: 'dashboard' }
];
