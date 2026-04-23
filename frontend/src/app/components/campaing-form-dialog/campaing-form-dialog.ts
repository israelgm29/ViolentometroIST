import { Component, Inject, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import {CampaignService} from "../../services/campaign.service";
import {CampaignCategory, CampaignDTO} from "../../models/campañing";
import {ToastrService} from "ngx-toastr";
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from "@angular/material/datepicker";


@Component({
  selector: 'app-campaing-form-dialog',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatButtonModule, MatIconModule,
    MatSlideToggleModule, MatProgressSpinnerModule, MatDatepickerToggle, MatDatepicker, MatDatepickerInput
  ],
  templateUrl: './campaing-form-dialog.html',
  styleUrls: ['./campaing-form-dialog.css']
})
export class CampaingFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private campaignService = inject(CampaignService);
  private dialogRef = inject(MatDialogRef<CampaingFormComponent>);
  private toastr = inject(ToastrService);

  loading = signal(false);
  categories = signal<CampaignCategory[]>([]);
  form!: FormGroup;

  constructor(@Inject(MAT_DIALOG_DATA) public data: CampaignDTO | null) {
    this.initForm();
  }

  ngOnInit() {
    this.loadCategories();
  }

  initForm() {
    this.form = this.fb.group({
      id: [this.data?.id || null],
      title: [this.data?.title || '', [Validators.required]],
      excerpt: [this.data?.excerpt || ''],
      imageUrl: [this.data?.imageUrl || ''],
      externalUrl: [this.data?.externalUrl || ''],
      categoryId: [this.data?.categoryId || null, [Validators.required]],

      publishDate: [this.data?.publishDate ? new Date(this.data.publishDate) : new Date()],
      startDate: [this.data?.startDate ? new Date(this.data.startDate) : null],
      endDate: [this.data?.endDate ? new Date(this.data.endDate) : null],

      featured: [this.data?.featured || false],
      status: [this.data?.status ?? true]
    });
  }

  loadCategories() {
    this.campaignService.getAllCategories().subscribe(cats => this.categories.set(cats));
  }

  save() {
    if (this.form.invalid) return;
    this.loading.set(true);

    const dto = this.form.value;
    const request = dto.id ? this.campaignService.update(dto.id, dto) : this.campaignService.create(dto);

    request.subscribe({
      next: (res) => {
        this.loading.set(false);
        this.toastr.success(dto.id ? 'Campaña actualizada' : 'Campaña publicada con éxito');
        this.dialogRef.close(res);
      },
      error: () => {
        this.loading.set(false);
        this.toastr.error('Error al procesar la campaña');
      }
    });
  }

  close() {
    this.dialogRef.close();
  }
}