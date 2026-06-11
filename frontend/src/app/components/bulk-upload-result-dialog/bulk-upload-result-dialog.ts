import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule }  from '@angular/material/button';
import { MatIconModule }    from '@angular/material/icon';
import { NgClass, DecimalPipe } from '@angular/common';
import {BulkUploadResult} from "../../services/bulk-upload.service";


@Component({
  selector:   'app-bulk-upload-result-dialog',
  standalone: true,
  imports:    [MatDialogModule, MatButtonModule, MatIconModule, NgClass, DecimalPipe],
  template: `
    <div class="result-dialog">

      <div class="result-header" [ngClass]="headerClass()">
        <div class="header-left">
          <div class="status-icon">
            <mat-icon>{{ headerIcon() }}</mat-icon>
          </div>
          <div>
            <h2>{{ headerTitle() }}</h2>
            <p>{{ data.totalRows }} fila(s) procesada(s) en total</p>
          </div>
        </div>
      </div>

      <mat-dialog-content>

        <div class="metrics-row">
          <div class="metric">
            <span class="metric-value created">{{ data.created }}</span>
            <div class="metric-label">
              <mat-icon>person_add</mat-icon>
              Creados
            </div>
          </div>
          <div class="metric-divider"></div>
          <div class="metric">
            <span class="metric-value updated">{{ data.updated }}</span>
            <div class="metric-label">
              <mat-icon>sync</mat-icon>
              Actualizados
            </div>
          </div>
          <div class="metric-divider"></div>
          <div class="metric">
            <span class="metric-value errors">{{ data.errors }}</span>
            <div class="metric-label">
              <mat-icon>error_outline</mat-icon>
              Errores
            </div>
          </div>
        </div>

        <div class="progress-bar-wrap">
          <div class="progress-segment success"
               [style.width.%]="getPercent(data.created + data.updated)">
          </div>
          <div class="progress-segment fail"
               [style.width.%]="getPercent(data.errors)">
          </div>
        </div>
        <div class="progress-labels">
          <span class="label-ok">{{ getPercent(data.created + data.updated) | number:'1.0-0' }}% procesado</span>
          <span class="label-fail">{{ getPercent(data.errors) | number:'1.0-0' }}% con error</span>
        </div>

        @if (data.rowErrors.length > 0) {
          <div class="errors-section">
            <div class="errors-header">
              <mat-icon>report_problem</mat-icon>
              <span>Filas con error ({{ data.rowErrors.length }})</span>
            </div>
            <div class="errors-table">
              <div class="errors-table-head">
                <span>Fila</span>
                <span>Cédula</span>
                <span>Motivo</span>
              </div>
              <div class="errors-table-body">
                @for (err of data.rowErrors; track err.row) {
                  <div class="error-row">
                    <span class="col-row">{{ err.row }}</span>
                    <span class="col-dni">{{ err.dni || '—' }}</span>
                    <span class="col-reason">{{ err.reason }}</span>
                  </div>
                }
              </div>
            </div>
          </div>
        }

      </mat-dialog-content>

      <mat-dialog-actions align="end">
        <button mat-flat-button class="btn-close" (click)="close()">Cerrar</button>
      </mat-dialog-actions>
    </div>
  `,
  styles: [`
    @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap');

    .result-dialog {
      min-width: 540px;
      font-family: 'Plus Jakarta Sans', sans-serif;
    }

    .result-header {
      padding: 20px 24px;
      border-bottom: 1px solid #e5e7eb;

      .header-left { display: flex; align-items: center; gap: 14px; }

      .status-icon {
        width: 44px; height: 44px;
        border-radius: 50%;
        display: flex; align-items: center; justify-content: center;
        flex-shrink: 0;
        mat-icon { font-size: 26px; width: 26px; height: 26px; }
      }

      h2 { margin: 0; font-size: 1.05rem; font-weight: 700; color: #111827; }
      p  { margin: 3px 0 0; font-size: 0.8rem; color: #4b5563; }

      &.all-ok  { background: #f0fdfa; .status-icon { background: #ccfbf1; mat-icon { color: #0d9488; } } h2, p { color: #0f766e; } }
      &.partial { background: #faf5ff; .status-icon { background: #ede9fe; mat-icon { color: #7c3aed; } } h2, p { color: #6d28d9; } }
      &.all-fail{ background: #fff1f2; .status-icon { background: #fee2e2; mat-icon { color: #ef4444; } } h2, p { color: #b91c1c; } }
    }

    .metrics-row {
      display: flex; align-items: center; justify-content: center;
      padding: 24px 0 16px;
    }

    .metric {
      flex: 1; display: flex; flex-direction: column; align-items: center; gap: 6px;

      .metric-value {
        font-size: 2.4rem; font-weight: 800; line-height: 1; letter-spacing: -1px;
        &.created { color: #0d9488; }
        &.updated { color: #7c3aed; }
        &.errors  { color: #ef4444; }
      }

      .metric-label {
        display: flex; align-items: center; gap: 4px;
        font-size: 0.75rem; font-weight: 600; color: #4b5563;
        text-transform: uppercase; letter-spacing: 0.5px;
        mat-icon { font-size: 14px; width: 14px; height: 14px; }
      }
    }

    .metric-divider { width: 1px; height: 48px; background: #e5e7eb; }

    .progress-bar-wrap {
      display: flex; height: 6px; border-radius: 4px;
      overflow: hidden; background: #f3f4f6; margin: 0 0 6px;
    }

    .progress-segment {
      height: 100%; transition: width 0.6s ease;
      &.success { background: #0d9488; }
      &.fail    { background: #ef4444; }
    }

    .progress-labels {
      display: flex; justify-content: space-between;
      font-size: 0.72rem; color: #9ca3af; margin-bottom: 20px;
      .label-ok   { color: #0d9488; font-weight: 600; }
      .label-fail { color: #ef4444; font-weight: 600; }
    }

    .errors-section { border: 1px solid #fecaca; border-radius: 10px; overflow: hidden; }

    .errors-header {
      display: flex; align-items: center; gap: 8px;
      padding: 10px 16px; background: #fff1f2;
      font-size: 0.85rem; font-weight: 600; color: #b91c1c;
      mat-icon { font-size: 18px; width: 18px; height: 18px; color: #ef4444; }
    }

    .errors-table { font-size: 0.82rem; }

    .errors-table-head {
      display: grid; grid-template-columns: 60px 120px 1fr;
      padding: 8px 16px; background: #f9fafb;
      color: #4b5563; font-weight: 700; font-size: 0.72rem;
      text-transform: uppercase; letter-spacing: 0.5px;
      border-bottom: 1px solid #e5e7eb;
    }

    .errors-table-body {
      max-height: 180px; overflow-y: auto;
      &::-webkit-scrollbar { width: 4px; }
      &::-webkit-scrollbar-thumb { background: #e5e7eb; border-radius: 2px; }
    }

    .error-row {
      display: grid; grid-template-columns: 60px 120px 1fr;
      padding: 9px 16px; border-bottom: 1px solid #f9fafb; align-items: center;
      &:last-child { border-bottom: none; }
      &:hover { background: #faf5ff; }
      .col-row    { font-weight: 700; color: #111827; }
      .col-dni    { font-weight: 600; color: #111827; font-family: monospace; }
      .col-reason { color: #ef4444; font-size: 0.8rem; }
    }

    mat-dialog-actions { padding: 16px 24px !important; border-top: 1px solid #e5e7eb; }

    .btn-close {
      background: linear-gradient(135deg, #7c3aed, #6d28d9) !important;
      color: white !important;
      padding: 0 24px !important;
      font-weight: 700 !important;
      border-radius: 12px !important;
      height: 48px !important;
      box-shadow: 0 8px 20px rgba(124, 58, 237, 0.28) !important;
    }
  `]
})
export class BulkUploadResultDialogComponent {
  constructor(
      @Inject(MAT_DIALOG_DATA) public data: BulkUploadResult,
      private dialogRef: MatDialogRef<BulkUploadResultDialogComponent>
  ) {}

  headerClass(): string {
    if (this.data.errors === 0) return 'all-ok';
    if (this.data.created + this.data.updated > 0) return 'partial';
    return 'all-fail';
  }

  headerIcon(): string {
    if (this.data.errors === 0) return 'check_circle';
    if (this.data.created + this.data.updated > 0) return 'warning';
    return 'cancel';
  }

  headerTitle(): string {
    if (this.data.errors === 0) return 'Carga exitosa';
    if (this.data.created + this.data.updated > 0) return 'Carga parcial';
    return 'Carga fallida';
  }

  getPercent(value: number): number {
    if (!this.data.totalRows) return 0;
    return (value / this.data.totalRows) * 100;
  }

  close() { this.dialogRef.close(); }
}