import {Component, inject, Inject, signal, computed} from '@angular/core';
import {FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule} from '@angular/forms';
import {MatDialogRef, MAT_DIALOG_DATA, MatDialogModule} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatRadioModule} from '@angular/material/radio';
import {MatButtonModule} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {Observable} from "rxjs";
import {AppUserResponse, AppUserRequest, MasterCatalog} from "../../models/app-user";
import {CatalogService} from "../../services/catalog.service";
import {AppUserService} from "../../services/app-user.service";
import {ToastrService} from "ngx-toastr";

@Component({
    selector: 'app-user-info-modal',
    standalone: true,
    imports: [
        ReactiveFormsModule,
        FormsModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatRadioModule,
        MatButtonModule,
        MatIcon,
        MatProgressSpinnerModule,
        MatDatepickerModule
    ],
    templateUrl: './user-info-modal.component.html',
    styleUrls: ['./user-info-modal.component.scss']
})
export class UserInfoModalComponent {

    // ── Formulario ────────────────────────────────────────────
    infoForm: FormGroup;

    // ── Servicios ─────────────────────────────────────────────
    private appUserService = inject(AppUserService);
    private toastr = inject(ToastrService);

    // ── Catálogos ─────────────────────────────────────────────
    genders = signal<MasterCatalog[]>([]);
    regions = signal<MasterCatalog[]>([]);
    ethnicities = signal<MasterCatalog[]>([]);
    disabilities = signal<MasterCatalog[]>([]);
    institutes = signal<MasterCatalog[]>([]);

    // ── Estado del modal ──────────────────────────────────────
    step = signal<1 | 2>(1);
    dni = signal('');
    loading = signal(false);
    dniTouched = signal(false);

    // Usuario encontrado en Step 1 (para saber si update o create en Step 2)
    private foundUser: AppUserResponse | null = null;

    // ── Validación DNI ────────────────────────────────────────
    isDniValid = computed(() => {
        const value = this.dni();
        return value.length === 10 && this.validateEcuadorianDNI(value);
    });

    isDniInvalid = computed(() => {
        return this.dniTouched() && this.dni().length > 0 && !this.isDniValid();
    });

    constructor(
        private fb: FormBuilder,
        private catalogService: CatalogService,
        public dialogRef: MatDialogRef<UserInfoModalComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any
    ) {
        this.infoForm = this.fb.group({
            birthdate: ['', Validators.required],
            idGender: ['', Validators.required],
            idRegion: ['', Validators.required],
            idEthnicity: ['', Validators.required],
            idInstitute: ['', Validators.required],
            hasDisability: ['no'],
            idDisability: [null],
        });

        this.infoForm.get('hasDisability')?.valueChanges.subscribe(value => {
            const disabilityControl = this.infoForm.get('idDisability');
            if (value === 'no') {
                disabilityControl?.clearValidators();
                disabilityControl?.setValue(null);
            } else {
                disabilityControl?.setValidators(Validators.required);
            }
            disabilityControl?.updateValueAndValidity();
        });
    }

    ngOnInit() {
        this.loadCatalogs();
    }

    loadCatalogs() {
        this.catalogService.findAll('genders').subscribe(data => this.genders.set(data));
        this.catalogService.findAll('regions').subscribe(data => this.regions.set(data));
        this.catalogService.findAll('ethnicities').subscribe(data => this.ethnicities.set(data));
        this.catalogService.findAll('disabilities').subscribe(data => this.disabilities.set(data));
        this.catalogService.findAll('institutes').subscribe(data => this.institutes.set(data));
    }

    // ── Algoritmo módulo 10 cédula ecuatoriana ────────────────
    validateEcuadorianDNI(dni: string): boolean {
        if (dni.length !== 10) return false;

        const province = parseInt(dni.substring(0, 2));
        if (province < 1 || province > 24) return false;

        const coefficients = [2, 1, 2, 1, 2, 1, 2, 1, 2];
        let sum = 0;

        for (let i = 0; i < 9; i++) {
            let value = parseInt(dni.charAt(i)) * coefficients[i];
            sum += value > 9 ? value - 9 : value;
        }

        const verifier = sum % 10 === 0 ? 0 : 10 - (sum % 10);
        return verifier === parseInt(dni.charAt(9));
    }

    onDniInput(event: any) {
        let value = event.target.value.replace(/\D/g, '');
        if (value.length > 10) value = value.substring(0, 10);
        this.dni.set(value);
        this.dniTouched.set(true);
    }

    // ── Step 1: verificar cédula ──────────────────────────────
    checkUser() {
        if (!this.isDniValid()) {
            this.toastr.warning('Por favor, ingrese un número de cédula válido.', 'Cédula Inválida');
            this.dniTouched.set(true);
            return;
        }

        this.loading.set(true);

        this.appUserService.validateAndLogUser(this.dni()).subscribe({
            next: (user: AppUserResponse | null) => {
                this.loading.set(false);

                if (!user) {
                    // Usuario no existe → error, se queda en Step 1
                    this.toastr.error(
                        'Usted no se encuentra registrado en el sistema. Por favor, contacte al administrador.',
                        'Acceso Denegado'
                    );
                    return;
                }

                // Guardar referencia para usarla en onSubmit()
                this.foundUser = user;

                if (this.isProfileComplete(user)) {
                    // ✅ Perfil completo → cierra directamente, va al quiz
                    this.dialogRef.close({userId: user.id, cedula: user.dni});
                } else {
                    // ⚠️ Perfil incompleto → pre-rellena y pasa al Step 2
                    this.toastr.info('Por favor, complete su perfil para continuar.', 'Información Faltante');
                    this.prefillForm(user);
                    this.goToStep2();
                }
            },
            error: () => {
                this.loading.set(false);
                this.toastr.error('Ocurrió un error al verificar sus datos. Intente más tarde.', 'Error de Conexión');
            }
        });
    }

    // Todos los campos obligatorios del perfil deben existir
    private isProfileComplete(user: AppUserResponse): boolean {
        return !!(
            user.gender?.id &&
            user.birthdate &&
            user.region?.id &&
            user.ethnicity?.id &&
            user.institute?.id &&
            user.disability?.id
        );
    }

    // Pre-rellena el formulario con los datos que ya existen
    private prefillForm(user: AppUserResponse) {
        this.infoForm.patchValue({
            birthdate: user.birthdate ?? null,
            idGender: user.gender?.id ?? null,
            idRegion: user.region?.id ?? null,
            idEthnicity: user.ethnicity?.id ?? null,
            idInstitute: user.institute?.id ?? null,
            hasDisability: user.disability?.id ? 'si' : 'no',
            idDisability: user.disability?.id ?? null,
        });
    }

    // ── Navegación entre steps con resize automático ──────────
    goToStep2() {
        this.step.set(2);
        this.dialogRef.updateSize('750px', 'auto');
    }

    goToStep1() {
        this.step.set(1);
        this.foundUser = null;
        this.dialogRef.updateSize('480px', 'auto');
    }

    // ── Step 2: guardar perfil ────────────────────────────────
    onSubmit() {
        if (this.infoForm.invalid) {
            this.infoForm.markAllAsTouched();
            return;
        }

        const form = this.infoForm.value;
        const user = this.foundUser;

        const request: AppUserRequest = {
            dni: this.dni(),
            idGender: form.idGender,
            birthdate: form.birthdate,
            idInstitute: form.idInstitute,
            idRegion: form.idRegion,
            idDisability: form.hasDisability === 'si' ? form.idDisability : 0,
            idEthnicity: form.idEthnicity,
        };

        this.loading.set(true);

        // Si el usuario ya existía → update, si no existía → create
        // Cast a Observable<any> para evitar conflicto entre los tipos de retorno
        const save$ = (user
            ? this.appUserService.updateAppUser(this.dni(), request)
            : this.appUserService.createAppUser(request)) as Observable<any>;

        save$.subscribe({
            next: () => {
                // Recargamos para obtener el objeto fresco con el id
                this.appUserService.getAppUserByDni(this.dni()).subscribe({
                    next: (freshUser) => {
                        this.loading.set(false);
                        if (freshUser) {
                            this.dialogRef.close({userId: freshUser.id, cedula: freshUser.dni});
                        }
                    },
                    error: () => {
                        this.loading.set(false);
                        this.toastr.error('Error al cargar el perfil actualizado.', 'Error');
                    }
                });
            },
            error: () => {
                this.loading.set(false);
                this.toastr.error('No se pudo guardar el perfil. Intente nuevamente.', 'Error');
            }
        });
    }

    onCancel(): void {
        this.dialogRef.close();
        window.location.reload();
    }
}