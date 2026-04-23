import {Component, Inject} from '@angular/core';
import {MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {
  MAT_DIALOG_DATA,
  MatDialogActions, MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";
import {MasterCatalog} from "../../models/app-user";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-catalog-form-dialog',
  template: `
    <h1 mat-dialog-title>{{ data.id ? 'Editar' : 'Nuevo' }} Registro</h1>
    <div mat-dialog-content>
      <mat-form-field appearance="outline" class="w-100">
        <mat-label>Nombre</mat-label>
        <input matInput [(ngModel)]="data.name" placeholder="Ej. Sierra, Auditiva, etc." required>
      </mat-form-field>
    </div>
    <div mat-dialog-actions align="end">
      <button mat-button (click)="onNoClick()">Cancelar</button>
      <button mat-raised-button color="primary" [mat-dialog-close]="data" [disabled]="!data.name">
        Guardar
      </button>
    </div>
  `,
  imports: [
    MatFormField,
    MatLabel,
    MatDialogContent,
    MatDialogTitle,
    MatDialogActions,
    MatButton,
    MatDialogClose,
    FormsModule,
    MatInput
  ],
  styles: [`.w-100 {
    width: 100%;
    margin-top: 10px;
  }`]
})
export class CatalogFormDialogComponent {
  constructor(
      public dialogRef: MatDialogRef<CatalogFormDialogComponent>,
      @Inject(MAT_DIALOG_DATA) public data: MasterCatalog
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }
}
