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
    <h2 mat-dialog-title class="dialog-title">
      <mat-icon>account_balance</mat-icon>
      Detalles del Instituto
    </h2>
    
    <mat-dialog-content class="dialog-content">
      <div class="detail-grid">
        <div class="detail-column full-width">
          <div class="section-divider">
            <mat-icon>business</mat-icon>
            <span>Información General</span>
          </div>
          <div class="info-list">
            <div class="info-item">
              <span class="info-label">Nombre del Instituto</span>
              <span class="info-value">{{ data.name }} ({{ data.shortName || 'N/A' }})</span>
            </div>
            <div class="info-item">
              <span class="info-label">Código</span>
              <span class="info-value">{{ data.code }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Estado</span>
              <span class="si-badge" [class.on]="data.status" [class.off]="!data.status">
                <mat-icon>{{ data.status ? 'check_circle' : 'remove_circle' }}</mat-icon>
                {{ data.status ? 'Activo' : 'Inactivo' }}
              </span>
            </div>
          </div>
        </div>

        <div class="section-divider full-width">
          <mat-icon>contact_mail</mat-icon>
          <span>Información de Contacto</span>
        </div>

        <div class="info-cards">
          <div class="info-card">
            <mat-icon>email</mat-icon>
            <div class="info-content">
              <span class="info-label">Correo Electrónico</span>
              <span class="info-value">{{ data.email || 'No especificado' }}</span>
            </div>
          </div>

          <div class="info-card">
            <mat-icon>phone</mat-icon>
            <div class="info-content">
              <span class="info-label">Teléfono</span>
              <span class="info-value">{{ data.phone || 'No especificado' }}</span>
            </div>
          </div>

          <div class="info-card">
            <mat-icon>location_on</mat-icon>
            <div class="info-content">
              <span class="info-label">Ubicación</span>
              <span class="info-value">{{ data.city || 'N/A' }}, {{ data.province || 'N/A' }} ({{ data.country || 'N/A' }})</span>
            </div>
          </div>

          <div class="info-card">
            <mat-icon>language</mat-icon>
            <div class="info-content">
              <span class="info-label">Sitio Web</span>
              <span class="info-value">
                @if (data.webUrl) {
                  <a [href]="data.webUrl" target="_blank">{{ data.webUrl }}</a>
                } @else {
                  No registrado
                }
              </span>
            </div>
          </div>
        </div>

        <div class="section-divider full-width">
          <mat-icon>schedule</mat-icon>
          <span>Información del Sistema</span>
        </div>

        <div class="info-item full-width">
          <span class="info-label">Fecha de Registro en Sistema</span>
          <span class="info-value">{{ data.createdDate | date:'fullDate' }} a las {{ data.createdDate | date:'shortTime' }}</span>
        </div>
      </div>
    </mat-dialog-content>

    <mat-dialog-actions align="end" class="dialog-actions">
      <button mat-flat-button class="save-btn" mat-dialog-close>
        <mat-icon>check_circle</mat-icon>
        Cerrar Detalle
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap');

    :host { font-family: 'Plus Jakarta Sans', sans-serif; }

    .dialog-title {
      display: flex;
      align-items: center;
      gap: 10px;
      font-family: 'Plus Jakarta Sans' !important;
      font-size: 1.1rem !important;
      font-weight: 800 !important;
      color: #111827;
      padding: 20px 24px 12px !important;
      margin: 0 !important;
      border-bottom: 1px solid #f1f0fb;
    }

    .dialog-content {
      padding: 20px 24px !important;
      max-height: 68vh !important;
      overflow-y: auto;
    }

    .detail-grid {
      display: grid;
      grid-template-columns: 1fr;
      gap: 20px;
    }

    .section-divider {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 10px 0 6px;
      font-size: 11px;
      font-weight: 700;
      color: #6b7280;
      text-transform: uppercase;
      letter-spacing: .06em;
      border-bottom: 1px solid #f1f0fb;
      margin-bottom: 12px;
    }

    .section-divider mat-icon {
      font-size: 15px;
      width: 15px;
      height: 15px;
      color: #7c3aed;
    }

    .full-width { grid-column: span 1; }

    .info-list {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .info-item {
      display: flex;
      flex-direction: column;
      gap: 2px;
      padding: 8px 0;
      border-bottom: 1px solid #f5f5f5;
    }

    .info-item:last-child { border-bottom: none; }

    .info-label {
      font-size: 11px;
      color: #6b7280;
      font-weight: 600;
    }

    .info-value {
      font-size: 13px;
      color: #111827;
      font-weight: 500;
    }

    .info-cards {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 12px;
      margin-top: 12px;
    }

    .info-card {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 15px;
      background: #fff;
      border: 1px solid #f1f0fb;
      border-left: 3px solid #7c3aed;
      border-radius: 12px;
      transition: all .2s ease;
    }

    .info-card:hover {
      background: #f8f7ff;
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(124, 58, 237, .08);
    }

    .info-card mat-icon {
      color: #7c3aed;
      font-size: 24px;
      width: 24px;
      height: 24px;
    }

    .info-content {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }

    .info-card .info-label {
      font-size: 10px;
      text-transform: uppercase;
      letter-spacing: .06em;
    }

    .si-badge {
      display: inline-flex;
      align-items: center;
      gap: 4px;
      padding: 4px 10px;
      border-radius: 12px;
      font-size: 11px;
      font-weight: 700;
    }

    .si-badge.on {
      background: #f0fdf4;
      color: #16a34a;
    }

    .si-badge.off {
      background: #fef2f2;
      color: #ef4444;
    }

    .si-badge mat-icon {
      font-size: 14px;
      width: 14px;
      height: 14px;
    }

    .dialog-actions {
      padding: 14px 24px !important;
      border-top: 1px solid #f1f0fb;
    }

    .save-btn {
      height: 36px !important;
      border-radius: 10px !important;
      font-family: 'Plus Jakarta Sans' !important;
      font-size: 12px !important;
      font-weight: 700 !important;
      background: #7c3aed !important;
      color: #fff !important;
      box-shadow: 0 4px 12px rgba(124, 58, 237, .25) !important;
    }

    .save-btn mat-icon { font-size: 16px; width: 16px; height: 16px; }
  `]
})
export class InstituteDetailDialog {
  constructor(
      public dialogRef: MatDialogRef<InstituteDetailDialog>,
      @Inject(MAT_DIALOG_DATA) public data: InterfaceInstitute
  ) {}
}