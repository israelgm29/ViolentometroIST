import {Component, Inject} from '@angular/core';
import {
    MAT_DIALOG_DATA,
    MatDialogModule,
    MatDialogRef
} from "@angular/material/dialog";
import {MatIconModule} from "@angular/material/icon";
import {MatInputModule} from "@angular/material/input";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatSelectModule} from "@angular/material/select";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatButtonModule} from "@angular/material/button";
import {CommonModule} from "@angular/common";
import {ZoneService} from "../../services/zone.service";
import {MatProgressBar} from "@angular/material/progress-bar";
import {ViolenceZoneInterface} from "../../models/zone";

@Component({
    selector: 'app-question-form-dialog',
    standalone: true,
    imports: [
        CommonModule, ReactiveFormsModule, MatDialogModule, MatFormFieldModule,
        MatInputModule, MatSelectModule, MatButtonModule, MatIconModule, MatSlideToggleModule, MatProgressBar
    ],
    templateUrl: './question-form-dialog.html',
    styleUrl: './question-form-dialog.css',
})
export class QuestionFormDialog {
    form: FormGroup;
    isEdit: boolean = false;
    zones: ViolenceZoneInterface[] = [];

    constructor(
        private fb: FormBuilder,
        private violenceZone: ZoneService,
        public dialogRef: MatDialogRef<QuestionFormDialog>,
        @Inject(MAT_DIALOG_DATA) public data: any
    ) {
        this.isEdit = !!data;
        this.form = this.fb.group({
            id: [data?.id || null],
            question: [data?.question || '', Validators.required],
            questionNumber: [data?.questionNumber || '', [Validators.required, Validators.min(1)]],
            idZone: [data?.zone?.id || data?.idZone || null, Validators.required],
            status: [data?.status ?? true]
        });
    }

    ngOnInit(): void {
        this.loadZones();
    }

    loadZones() {
        this.violenceZone.getAllZones().subscribe(res => this.zones = res);
    }

    save() {
        if (this.form.valid) this.dialogRef.close(this.form.value);
    }

    close() {
        this.dialogRef.close();
    }


    getColorHex(colorName: string): string {
        const colors: { [key: string]: string } = {
            'verde': '#2e7d32',    // Verde Material
            'amarillo': '#fbc02d', // Amarillo Material
            'rojo': '#d32f2f',     // Rojo Material
            'naranja': '#ef6c00'
        };
        // Si el colorName existe en el mapa lo devuelve, si no, devuelve el string tal cual
        return colors[colorName.toLowerCase()] || colorName;
    }

}
