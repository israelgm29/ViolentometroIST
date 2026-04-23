import { Component, signal } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { fadeAnimation } from './route-animations';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterModule,
    RouterOutlet
  ],
  templateUrl: './app.html',
  styleUrl: './app.css',
  animations: [fadeAnimation]
})
export class App {
  protected readonly title = signal('frontend');

  prepareRoute(outlet: RouterOutlet) {
    return outlet && outlet.activatedRouteData && outlet.activatedRouteData['animation'];
  }
}
