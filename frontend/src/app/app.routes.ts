import { Routes } from '@angular/router';
import { MapaPageComponent } from './components/mapa-page/mapa-page.component';

export const routes: Routes = [
  {
    path: '',
    component: MapaPageComponent
  },
  {
    path: 'repartidor',
    loadComponent: () =>
      import('./components/repartidor-page/repartidor-page.component').then(
        m => m.RepartidorPageComponent
      )
  },
  {
    path: '**',
    redirectTo: ''
  }
];
