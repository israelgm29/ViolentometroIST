import { Component, Inject } from '@angular/core';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule, MatIconModule],
  template: `
    <h2 mat-dialog-title class="dialog-title">
      <mat-icon color="warn">warning</mat-icon> <span>Confirmar Acción</span>
    </h2>

    <mat-dialog-content>
      <p style="margin-top: 10px;">
        {{ data.message || '¿Estás seguro de que deseas realizar esta acción?' }}
      </p>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onNoClick()">Cancelar</button>
      <button mat-flat-button color="warn" [mat-dialog-close]="true">Confirmar</button>
    </mat-dialog-actions>
  `,
  styles: [`
    .dialog-title {
      display: flex;
      align-items: center;
      gap: 12px;
      color: #f44336; /* Color de advertencia de Material */
    }
    .dialog-title mat-icon {
      font-size: 28px;
      width: 28px;
      height: 28px;
    }
  `]
})
export class ConfirmDialog {
  constructor(
      public dialogRef: MatDialogRef<ConfirmDialog>,
      @Inject(MAT_DIALOG_DATA) public data: { message: string }
  ) {}

  onNoClick(): void {
    this.dialogRef.close(false);
  }
}