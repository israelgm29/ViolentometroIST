import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import {InterfaceInstitute} from "../../models/institute";

@Component({
  selector: 'app-institute-detail',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule
  ],
  template: `
    <h2 mat-dialog-title class="detail-title">
      <mat-icon color="primary">account_balance</mat-icon>
      Detalles del Instituto
    </h2>
    
    <mat-dialog-content class="detail-container">
      <div class="info-grid">
        <div class="info-item full-width">
          <label>Nombre del Instituto</label>
          <p>{{ data.name }} ({{ data.shortName || 'N/A' }})</p>
        </div>

        <div class="info-item">
          <label>Código</label>
          <p>{{ data.code }}</p>
        </div>

        <div class="info-item">
          <label>Estado</label>
          <p>
            <span [class]="data.status ? 'status-pill active' : 'status-pill inactive'">
              {{ data.status ? 'ACTIVO' : 'INACTIVO' }}
            </span>
          </p>
        </div>

        <mat-divider class="full-width"></mat-divider>

        <div class="info-item">
          <label><mat-icon inline>email</mat-icon> Correo Electrónico</label>
          <p>{{ data.email }}</p>
        </div>

        <div class="info-item">
          <label><mat-icon inline>phone</mat-icon> Teléfono</label>
          <p>{{ data.phone }}</p>
        </div>

        <div class="info-item">
          <label><mat-icon inline>location_on</mat-icon> Ubicación</label>
          <p>{{ data.city }}, {{ data.province }} ({{ data.country }})</p>
        </div>

        <div class="info-item">
          <label><mat-icon inline>public</mat-icon> Sitio Web</label>
          <p><a [href]="data.webUrl" target="_blank">{{ data.webUrl || 'No registrado' }}</a></p>
        </div>

        <mat-divider class="full-width"></mat-divider>

        <div class="info-item full-width footer-info">
          <label>Fecha de Registro en Sistema</label>
          <p>{{ data.createdDate | date:'fullDate' }} a las {{ data.createdDate | date:'shortTime' }}</p>
        </div>
      </div>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-flat-button color="primary" mat-dialog-close>Cerrar Detalle</button>
    </mat-dialog-actions>
  `,
  styles: [`
    .detail-title { display: flex; align-items: center; gap: 10px; font-weight: bold; }
    .detail-container { min-width: 500px; padding-top: 15px !important; }
    .info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
    .full-width { grid-column: span 2; }
    
    label { 
      display: block; 
      font-size: 11px; 
      text-transform: uppercase; 
      color: #666; 
      font-weight: 600;
      margin-bottom: 4px;
      display: flex;
      align-items: center;
      gap: 4px;
    }
    
    p { margin: 0; font-size: 15px; color: #333; font-weight: 500; }
    
    .status-pill {
      padding: 2px 10px;
      border-radius: 12px;
      font-size: 11px;
      font-weight: bold;
    }
    .active { background: #e8f5e9; color: #2e7d32; }
    .inactive { background: #ffebee; color: #c62828; }
    .footer-info p { font-style: italic; color: #888; font-size: 13px; }
  `]
})
export class InstituteDetailDialog {
  constructor(
      public dialogRef: MatDialogRef<InstituteDetailDialog>,
      @Inject(MAT_DIALOG_DATA) public data: InterfaceInstitute
  ) {}
}