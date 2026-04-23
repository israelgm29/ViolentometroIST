import {Component, Inject} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {InterfaceInstitute} from "../../models/institute";
import {SysRoleInterface} from "../../models/sys-role";
import {InstituteService} from "../../services/institute.service";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogModule,
  MatDialogRef
} from "@angular/material/dialog";
import {SysUserInterface} from "../../models/sys-user";
import {SysRoleService} from "../../services/sys-role.service";
import {MatInput, MatLabel} from "@angular/material/input";
import {MatSelectModule} from "@angular/material/select";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatOptionModule} from "@angular/material/core";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-user-form-dialog',
  standalone: true,
  imports: [
    MatDialogModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatLabel,
    MatSelectModule,
    MatOptionModule,
    MatDialogActions,
    MatButton,
    MatInput,
    MatIcon,
  ],
  templateUrl: './user-form-dialog.html',
  styleUrl: './user-form-dialog.css',
})
export class UserFormDialog {
  userForm: FormGroup;

  // Ahora usamos tipado estricto en lugar de any[]
  roles: SysRoleInterface[] = [];
  institutes: InterfaceInstitute[] = [];

  isEditMode: boolean = false;

  constructor(
      private fb: FormBuilder,
      private roleService: SysRoleService,
      private instituteService: InstituteService,
      public dialogRef: MatDialogRef<UserFormDialog>,
      // Tipamos la data que entra al diálogo
      @Inject(MAT_DIALOG_DATA) public data: SysUserInterface | null
  ) {
    this.isEditMode = !!data;
    this.userForm = this.createForm();
  }

  ngOnInit(): void {
    this.loadCatalogos();
    if (this.isEditMode) {
      // Si es edición, cargamos los IDs en el formulario
      this.userForm.patchValue({
        ...this.data,
        idRole: this.data?.role.id,       // Extraemos el ID del objeto de respuesta
        idInstitute: this.data?.institute.id
      });
    }
  }

  createForm(): FormGroup {
    return this.fb.group({
      firstname: ['', [Validators.required]],
      secondname: [''],
      firstLastname: ['', [Validators.required]],
      secondLastname: [''],
      dni: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(10)]],
      phone: [''],
      email: ['', this.isEditMode? []: [Validators.required, Validators.email]],
      address: [''],
      idRole: [null, [Validators.required]],
      idInstitute: [null, [Validators.required]],
      // Solo obligatorio si es creación
      password: ['', this.isEditMode ? [] : [Validators.required, Validators.minLength(8)]]
    });
  }

  loadCatalogos() {
    this.roleService.getSysRoles().subscribe(res => this.roles = res);
    this.instituteService.getInstitutes().subscribe(res => this.institutes = res);
  }

  save() {
    if (this.userForm.valid) {
      this.dialogRef.close(this.userForm.value);
    }
  }
}
