import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Face3DComponent } from './features/violentometro/components/face3d/face3d'; // ✓ Ruta exacta

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Face3DComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class AppComponent { // ✓ Usa AppComponent (convención Angular)
  title = signal('violentometro');
}
