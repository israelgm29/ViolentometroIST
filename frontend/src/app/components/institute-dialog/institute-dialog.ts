import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ToastrService } from 'ngx-toastr';

import { InterfaceInstitute } from '../../models/institute';
import { InstituteService } from '../../services/institute.service';

@Component({
  selector: 'app-institute-dialog',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule,
    MatDialogModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatIconModule, MatProgressSpinnerModule, MatTooltipModule
  ],
  templateUrl: './institute-dialog.html',
  styleUrl:    './institute-dialog.css'
})
export class InstituteDialog implements OnInit {
  form:        FormGroup;
  isEdit:      boolean = false;

  // ── Logo ────────────────────────────────────────────────────────
  logoPreview:    string | null = null;
  selectedFile:   File | null   = null;
  uploadingLogo:  boolean       = false;
  hasExistingLogo: boolean      = false;

  constructor(
      private fb:              FormBuilder,
      private dialogRef:       MatDialogRef<InstituteDialog>,
      private instituteService: InstituteService,
      private toastr:          ToastrService,
      @Inject(MAT_DIALOG_DATA) public data: InterfaceInstitute
  ) {
    this.isEdit = !!data;
    this.hasExistingLogo = !!data?.id && !!data?.hasLogo;

    this.form = this.fb.group({
      id:        [data?.id || null],
      code:      [data?.code      || '', [Validators.required]],
      name:      [data?.name      || '', [Validators.required]],
      shortName: [data?.shortName || ''],
      address:   [data?.address   || '', [Validators.required]],
      city:      [data?.city      || '', [Validators.required]],
      province:  [data?.province  || '', [Validators.required]],
      country:   [data?.country   || 'Ecuador', [Validators.required]],
      phone:     [data?.phone     || '', [Validators.required]],
      email:     [data?.email     || '', [Validators.required, Validators.email]],
      webUrl:    [data?.webUrl    || '']
    });
  }

  ngOnInit(): void {
    // Si ya tiene logo, mostrar preview desde el endpoint
    if (this.hasExistingLogo && this.data?.id) {
      this.logoPreview = this.instituteService.getLogoUrl(this.data.id);
    }
  }

  // ── Selección de archivo ──────────────────────────────────────────
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;

    const file = input.files[0];

    // Validar tipo
    if (!file.type.startsWith('image/')) {
      this.toastr.error('Solo se permiten imágenes (PNG, JPG, SVG)', 'Formato inválido');
      return;
    }

    // Validar tamaño (1MB)
    if (file.size > 1_048_576) {
      this.toastr.error('El logo no puede superar 1MB', 'Archivo muy grande');
      return;
    }

    this.selectedFile = file;

    // Preview local
    const reader = new FileReader();
    reader.onload = (e) => {
      this.logoPreview = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  }

  removeLogo(): void {
    this.selectedFile    = null;
    this.logoPreview     = null;
    this.hasExistingLogo = false;
  }

  // ── Guardar ───────────────────────────────────────────────────────
  save(): void {
    if (this.form.invalid) return;

    const formValue = this.form.value;

    // Si hay archivo seleccionado y ya existe el instituto, subir logo
    if (this.selectedFile && this.data?.id) {
      this.uploadingLogo = true;
      this.instituteService.uploadLogo(this.data.id, this.selectedFile).subscribe({
        next: () => {
          this.uploadingLogo = false;
          this.dialogRef.close(formValue);
        },
        error: () => {
          this.uploadingLogo = false;
          this.toastr.error('No se pudo subir el logo', 'Error');
          // Igual cerramos con los datos del form
          this.dialogRef.close(formValue);
        }
      });
    } else {
      // Sin logo nuevo, cerrar normalmente
      // El logo se subirá después del save desde institutes.ts
      this.dialogRef.close({ ...formValue, _pendingLogo: this.selectedFile });
    }
  }

  close(): void {
    this.dialogRef.close();
  }
}