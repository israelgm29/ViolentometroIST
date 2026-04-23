import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import {MatIcon} from "@angular/material/icon";
import {InterfaceInstitute} from "../../models/institute";

@Component({
  selector: 'app-institute-dialog',
  standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatIcon
    ],
  templateUrl: './institute-dialog.html',
  styleUrl: './institute-dialog.css'
})
export class InstituteDialog implements OnInit {
  form: FormGroup;
  isEdit: boolean = false;

  constructor(
      private fb: FormBuilder,
      private dialogRef: MatDialogRef<InstituteDialog>,
      @Inject(MAT_DIALOG_DATA) public data: InterfaceInstitute // Recibe el objeto instituto o null
  ) {
    this.isEdit = !!data;
    this.form = this.fb.group({
      id: [data?.id || null],
      code: [data?.code || '', [Validators.required]],
      name: [data?.name || '', [Validators.required]],
      shortName: [data?.shortName || ''],
      address: [data?.address || '', [Validators.required]],
      city: [data?.city || '', [Validators.required]],
      province: [data?.province || '', [Validators.required]],
      country: [data?.country || 'Ecuador', [Validators.required]],
      phone: [data?.phone || '', [Validators.required]],
      email: [data?.email || '', [Validators.required, Validators.email]],
      webUrl: [data?.webUrl || '']
    });
  }

  ngOnInit(): void {}

  save() {
    if (this.form.valid) {
      this.dialogRef.close(this.form.value);
    }
  }

  close() {
    this.dialogRef.close();
  }
}