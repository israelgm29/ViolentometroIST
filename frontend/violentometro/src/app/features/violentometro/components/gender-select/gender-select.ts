import { Component, output } from '@angular/core';
import { IonicModule } from '@ionic/angular/standalone';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-gender-select',
  standalone: true,
  imports: [IonicModule, CommonModule],
  template: `
    <ion-card class="gender-card">
      <ion-card-header>
        <ion-card-title>Selecciona tu género</ion-card-title>
      </ion-card-header>
      <ion-card-content>
        <ion-button expand="block" fill="outline" color="secondary" (click)="select('mujer')">
          Mujer
        </ion-button>
        <ion-button expand="block" fill="outline" color="secondary" (click)="select('hombre')">
          Hombre
        </ion-button>
      </ion-card-content>
    </ion-card>
  `,
  styles: [`
    .gender-card {
      max-width: 300px;
      margin: auto;
      border-radius: 20px;
      box-shadow: 0 8px 32px rgba(0,0,0,0.1);
    }
  `]
})
export class GenderSelectComponent {
  onSelect = output<string>();

  select(gender: string) {
    this.onSelect.emit(gender);
  }
}
