import { Component, Inject, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner'; // Añadido para el loading
import { CampaignService } from '../../services/campaign.service';
import { CampaignCategory } from '../../models/campañing';
import { ToastrService } from 'ngx-toastr'; // <--- IMPORTACIÓN

@Component({
    selector: 'app-campaing-category-form',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatIconModule,
        MatSlideToggleModule,
        MatProgressSpinnerModule
    ],
    templateUrl: './campaing-category-form.html',
    styleUrls: ['./campaing-category-form.css']
})
export class CampaingCategoryForm implements OnInit {
    private fb = inject(FormBuilder);
    private campaignService = inject(CampaignService);
    private toastr = inject(ToastrService); // <--- INYECCIÓN
    private dialogRef = inject(MatDialogRef<CampaingCategoryForm>);

    loading = signal(false); // Signal para controlar el estado de carga
    form!: FormGroup;

    constructor(@Inject(MAT_DIALOG_DATA) public data: CampaignCategory | null) {
        this.initForm();
    }

    ngOnInit() {}

    initForm() {
        this.form = this.fb.group({
            id: [this.data?.id || null],
            name: [this.data?.name || '', [Validators.required, Validators.minLength(3)]],
            color: [this.data?.color || '#6d28d9'],
            icon: [this.data?.icon || 'label'],
            status: [this.data?.status ?? true]
        });
    }

    save() {
        if (this.form.invalid) return;

        this.loading.set(true); // Iniciamos el spinner
        const categoryData: CampaignCategory = this.form.value;

        const request = categoryData.id
            ? this.campaignService.updateCategory(categoryData.id, categoryData)
            : this.campaignService.createCategory(categoryData);

        request.subscribe({
            next: (res) => {
                this.loading.set(false);
                this.toastr.success(
                    categoryData.id ? 'Categoría actualizada' : 'Categoría creada con éxito',
                    '¡Excelente!'
                );
                this.dialogRef.close(res);
            },
            error: (err) => {
                this.loading.set(false);
                this.toastr.error('Hubo un problema al procesar la solicitud', 'Error');
                console.error('Error:', err);
            }
        });
    }

    close() {
        this.dialogRef.close();
    }
}