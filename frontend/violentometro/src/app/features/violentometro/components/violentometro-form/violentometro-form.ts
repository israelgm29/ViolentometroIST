import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonHeader, IonToolbar, IonTitle, IonContent, IonInput, IonList, IonItem, IonLabel, IonToggle, IonButton } from '@ionic/angular/standalone';
import { ViolentometroService } from '../../services/violentometro';
import { NivelRiesgo } from '../../models/nivel-riesgo.model';

@Component({
  selector: 'app-violentometro-form',
  standalone: true,
  imports: [CommonModule, FormsModule, IonHeader, IonToolbar, IonTitle, IonContent, IonInput, IonList, IonItem, IonLabel, IonToggle, IonButton],
  template: `
    <ion-header>
      <ion-toolbar color="primary">
        <ion-title>Violentómetro</ion-title>
      </ion-toolbar>
    </ion-header>
    <ion-content class="ion-padding">
      <ion-input [(ngModel)]="cedula" placeholder="Ingresa cédula" (ionChange)="loadPersona()"></ion-input>
      <ion-list *ngIf="service.preguntas().length > 0">
        <ion-item *ngFor="let pregunta of service.preguntas()">
          <ion-label>{{ pregunta.texto }}</ion-label>
          <ion-toggle [(ngModel)]="service.respuestas()[pregunta.id]" (ionChange)="service.toggleRespuesta(pregunta.id, $event.detail.checked)"></ion-toggle>
        </ion-item>
      </ion-list>
      <ion-button expand="block" (click)="guardar()" [color]="getColorRiesgo()">
        Guardar ({{ service.puntuacionTotal() }} pts - {{ service.getNivelRiesgo(service.puntuacionTotal()) }})
      </ion-button>
    </ion-content>
  `,
  styles: [`
    ion-content {
      --background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
  `]
})
export class ViolentometroFormComponent implements OnInit {
  service = inject(ViolentometroService);
  cedula = '';

  ngOnInit() {
    this.service.loadPreguntas();
  }

  loadPersona() {
    if (this.cedula.length < 10) return;
    // TODO: Implementar llamada API para cargar datos
  }

  guardar() {
    if (!this.cedula) {
      alert('⚠️ Ingresa la cédula primero');
      return;
    }
    this.service.guardarViolentometro(this.cedula);
    alert('✅ Guardado exitosamente!');
  }

  getColorRiesgo(): 'success' | 'warning' | 'danger' | 'primary' {
    const riesgo = this.service.getNivelRiesgo(this.service.puntuacionTotal());
    switch (riesgo) {
      case 'BAJO': return 'success';
      case 'MEDIO': return 'warning';
      case 'ALTO': return 'danger';
      default: return 'primary';
    }
  }
}
