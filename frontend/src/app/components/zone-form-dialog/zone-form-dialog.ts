import { Component, Inject, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { CommonModule } from "@angular/common";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatTooltipModule } from '@angular/material/tooltip';
import { ColorPickerDirective } from 'ngx-color-picker';

@Component({
  selector: 'app-zone-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    ColorPickerDirective
  ],
  templateUrl: './zone-form-dialog.html',
  styleUrl: './zone-form-dialog.scss',
})
export class ZoneFormDialog implements OnInit {
  form: FormGroup;
  isEdit: boolean = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<ZoneFormDialog>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', Validators.required],
      color: ['#3f51b5', Validators.required],
      severity: [1, [Validators.required, Validators.min(1), Validators.max(10)]],
      resultTitle: [''],
      resultMessage: [''],
      recommendations: this.fb.array([])
    });
  }

  ngOnInit(): void {
    if (this.data) {
      this.isEdit = true;

      this.form.patchValue({
        name: this.data.name,
        description: this.data.description,
        color: this.data.color,
        severity: this.data.severity,
        resultTitle: this.data.resultTitle || '',
        resultMessage: this.data.resultMessage || '',
      });

      if (this.data.recommendations?.length) {
        this.data.recommendations.forEach((rec: string) => {
          this.recommendations.push(this.fb.control(rec, Validators.required));
        });
      }
    }
  }

  get recommendations(): FormArray {
    return this.form.get('recommendations') as FormArray;
  }

  getRecControl(index: number): FormControl {
    return this.recommendations.at(index) as FormControl;
  }

  addRecommendation(): void {
    this.recommendations.push(this.fb.control('', Validators.required));
  }

  removeRecommendation(index: number): void {
    this.recommendations.removeAt(index);
  }

  save(): void {
    if (this.form.valid) {
      const value = this.form.value;
      this.dialogRef.close({
        ...value,
        recommendations: value.recommendations.filter((r: string) => r?.trim())
      });
    }
  }
}