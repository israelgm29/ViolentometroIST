import {Component, Inject} from '@angular/core';
import {MasterCatalog} from "../../models/app-user";
import {InterfaceInstitute} from "../../models/institute";
import {
    MAT_DIALOG_DATA,
    MatDialogActions,
    MatDialogModule,
    MatDialogRef
} from "@angular/material/dialog";
import {CatalogService} from "../../services/catalog.service";
import {InstituteService} from "../../services/institute.service";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInput, MatLabel} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatNativeDateModule, MatOptionModule} from "@angular/material/core";
import {MatSelectModule} from "@angular/material/select";
import {
    MatDatepickerModule
} from "@angular/material/datepicker";


@Component({
    selector: 'app-app-user-form-dialog',
    standalone: true,
    imports: [MatDialogModule, ReactiveFormsModule, MatFormFieldModule, MatLabel,
        MatSelectModule, MatOptionModule, MatDialogActions, MatButton,
        MatInput, MatIcon, MatDatepickerModule, MatNativeDateModule
    ],
    templateUrl: './app-user-form-dialog.html',
    styleUrl: './app-user-form-dialog.css',
})
export class AppUserFormDialog {
    appUserForm: FormGroup;
    isEditMode: boolean = false;

    // Catálogos
    regions: MasterCatalog[] = [];
    disabilities: MasterCatalog[] = [];
    ethnicities: MasterCatalog[] = [];
    institutes: InterfaceInstitute[] = [];
    genders: MasterCatalog[] = [];

    constructor(
        private fb: FormBuilder,
        private catalogService: CatalogService,
        private instituteService: InstituteService,
        public dialogRef: MatDialogRef<AppUserFormDialog>,
        @Inject(MAT_DIALOG_DATA) public data: any | null
    ) {
        this.isEditMode = !!data;
        this.appUserForm = this.createForm();
    }


    ngOnInit(): void {
        this.loadCatalogos();
        if (this.isEditMode) {
            this.appUserForm.patchValue({
                ...this.data,
                idInstitute: this.data?.institute?.id,
                idRegion: this.data?.region?.id,
                idDisability: this.data?.disability?.id,
                idEthnicity: this.data?.ethnicity?.id,
                idGender: this.data?.gender?.id
            });
        }
    }

    createForm(): FormGroup {
        return this.fb.group({
            dni: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(10)]],
            idGender: ['', [Validators.required]],
            birthdate: ['', [Validators.required]],
            idInstitute: [null, [Validators.required]],
            idRegion: [null, [Validators.required]],
            idDisability: [null, [Validators.required]],
            idEthnicity: [null, [Validators.required]]
        });
    }

    loadCatalogos() {
        this.catalogService.findAll('regions').subscribe(res => this.regions = res);
        this.catalogService.findAll('disabilities').subscribe(res => this.disabilities = res);
        this.catalogService.findAll('ethnicities').subscribe(res => this.ethnicities = res);
        this.catalogService.findAll('genders').subscribe(res => this.genders = res);
        this.instituteService.getInstitutes().subscribe(res => this.institutes = res);
    }

    save() {
        if (this.appUserForm.valid) {
            this.dialogRef.close(this.appUserForm.value);
        }
    }

    close() {
        this.dialogRef.close();
    }
}
