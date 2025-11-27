import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/violentometro-form/violentometro-form').then(m => m.ViolentometroFormComponent)
  },
  {
    path: 'gender',
    loadComponent: () => import('./components/gender-select/gender-select').then(m => m.GenderSelectComponent)
  }
];
