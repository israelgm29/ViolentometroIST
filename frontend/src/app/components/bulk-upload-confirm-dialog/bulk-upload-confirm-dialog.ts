import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule }   from '@angular/material/icon';

export interface BulkUploadConfirmData {
  fileName: string;
  fileSize: string;
}

@Component({
  selector:   'app-bulk-upload-confirm-dialog',
  standalone: true,
  imports:    [MatDialogModule, MatButtonModule, MatIconModule],
  template: `
    <div class="confirm-dialog">
        <div class="confirm-header">
            <div class="header-icon">
                <mat-icon>upload_file</mat-icon>
            </div>
            <div class="header-text">
                <h2>Confirmar carga masiva</h2>
                <p>Revisa el archivo antes de continuar</p>
            </div>
        </div>

        <mat-dialog-content>
            <div class="file-preview">
                <div class="file-icon">
                    <mat-icon>{{ data.fileName.endsWith('.csv') ? 'csv' : 'table_chart' }}</mat-icon>
                </div>
                <div class="file-info">
                    <span class="file-name">{{ data.fileName }}</span>
                    <span class="file-size">{{ data.fileSize }}</span>
                </div>
            </div>

            <div class="warning-box">
                <mat-icon>info</mat-icon>
                <div>
                    <strong>¿Qué ocurrirá?</strong>
                    <ul>
                        <li>Los estudiantes nuevos serán <strong>creados</strong> automáticamente.</li>
                        <li>Los estudiantes existentes serán <strong>actualizados</strong>.</li>
                        <li>Las filas con errores serán <strong>omitidas</strong> y reportadas.</li>
                    </ul>
                </div>
            </div>
        </mat-dialog-content>

        <mat-dialog-actions align="end">
            <button mat-stroked-button (click)="cancel()">
                <mat-icon>close</mat-icon>
                Cancelar
            </button>
            <button mat-flat-button class="btn-confirm" (click)="confirm()">
                <mat-icon>check</mat-icon>
                Confirmar carga
            </button>
        </mat-dialog-actions>
    </div>
    `,
  styles: [`
        @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap');

        .confirm-dialog {
            min-width: 480px;
            font-family: 'Plus Jakarta Sans', sans-serif;
        }

        .confirm-header {
            display: flex;
            align-items: center;
            gap: 16px;
            padding: 24px 24px 0;

            .header-icon {
                width: 48px;
                height: 48px;
                border-radius: 12px;
                background: linear-gradient(135deg, #ede9fe, #faf5ff);
                display: flex;
                align-items: center;
                justify-content: center;
                flex-shrink: 0;
                box-shadow: 0 8px 20px rgba(124, 58, 237, 0.15);

                mat-icon {
                    color: #7c3aed;
                    font-size: 26px;
                    width: 26px;
                    height: 26px;
                }
            }

            .header-text {
                h2 { margin: 0; font-size: 1.1rem; font-weight: 700; color: #111827; }
                p  { margin: 4px 0 0; font-size: 0.85rem; color: #4b5563; }
            }
        }

        .file-preview {
            display: flex;
            align-items: center;
            gap: 14px;
            padding: 16px;
            background: #faf5ff;
            border: 1px solid #ede9fe;
            border-radius: 12px;
            margin-bottom: 16px;

            .file-icon {
                width: 40px;
                height: 40px;
                background: #ede9fe;
                border-radius: 8px;
                display: flex;
                align-items: center;
                justify-content: center;

                mat-icon { color: #7c3aed; font-size: 22px; width: 22px; height: 22px; }
            }

            .file-info {
                display: flex;
                flex-direction: column;
                gap: 2px;

                .file-name { font-weight: 600; font-size: 0.9rem; color: #111827; }
                .file-size { font-size: 0.78rem; color: #4b5563; }
            }
        }

        .warning-box {
            display: flex;
            gap: 12px;
            padding: 14px 16px;
            background: #faf5ff;
            border: 1px solid #ede9fe;
            border-radius: 12px;
            font-size: 0.85rem;
            color: #6d28d9;

            mat-icon { color: #7c3aed; flex-shrink: 0; margin-top: 2px; }

            strong { display: block; margin-bottom: 6px; font-size: 0.875rem; color: #6d28d9; }

            ul {
                margin: 0;
                padding-left: 18px;
                display: flex;
                flex-direction: column;
                gap: 4px;
                color: #4b5563;
            }
        }

        mat-dialog-actions {
            padding: 16px 24px !important;
            gap: 10px;
        }

        .btn-confirm {
            background: linear-gradient(135deg, #7c3aed, #6d28d9) !important;
            color: white !important;
            font-weight: 700 !important;
            border-radius: 12px !important;
            height: 48px !important;
            box-shadow: 0 8px 20px rgba(124, 58, 237, 0.28) !important;
        }
    `]
})
export class BulkUploadConfirmDialogComponent {
  constructor(
      @Inject(MAT_DIALOG_DATA) public data: BulkUploadConfirmData,
      private dialogRef: MatDialogRef<BulkUploadConfirmDialogComponent>
  ) {}

  confirm() { this.dialogRef.close(true);  }
  cancel()  { this.dialogRef.close(false); }
}