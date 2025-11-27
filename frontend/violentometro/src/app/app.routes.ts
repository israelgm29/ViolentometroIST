import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: '/violentometro', pathMatch: 'full' },
  { path: 'violentometro', loadChildren: () => import('./features/violentometro/violentometro.routes').then(m => m.routes) },
  { path: '**', redirectTo: '/violentometro' }  // Wildcard para rutas inválidas
];
