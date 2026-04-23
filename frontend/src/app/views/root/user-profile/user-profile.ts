import {Component, inject, signal} from '@angular/core';
import {ProfileService} from "../../../services/profile.service";
import {AbstractControl, FormBuilder, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {ProfileResponse} from "../../../models/sys-user";
import {MatIcon} from "@angular/material/icon";
import {MatProgressSpinner} from "@angular/material/progress-spinner";

@Component({
    selector: 'app-user-profile',
    imports: [
        FormsModule,
        MatIcon,
        ReactiveFormsModule,
        MatProgressSpinner
    ],
    templateUrl: './user-profile.html',
    styleUrl: './user-profile.scss',
})
export class UserProfile {
    private profileService = inject(ProfileService);
    private fb = inject(FormBuilder);

    profile = signal<ProfileResponse | null>(null);
    loading = signal(true);
    savingProfile = signal(false);
    savingPassword = signal(false);
    profileSuccess = signal(false);
    passwordSuccess = signal(false);
    profileError = signal('');
    passwordError = signal('');
    showCurrentPwd = signal(false);
    showNewPwd = signal(false);
    showConfirmPwd = signal(false);

    profileForm = this.fb.group({
        firstname: ['', Validators.required],
        secondname: [''],
        firstLastname: ['', Validators.required],
        secondLastname: [''],
        email: ['', [Validators.required, Validators.email]],
        phone: [''],
        address: ['']
    });

    passwordForm = this.fb.group({
        currentPassword: ['', Validators.required],
        newPassword: ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', Validators.required]
    }, {validators: this.passwordMatchValidator});

    ngOnInit() {
        this.loadProfile();
    }

    loadProfile() {
        this.loading.set(true);
        this.profileService.getProfile().subscribe({
            next: (data) => {
                this.profile.set(data);
                this.profileForm.patchValue(data);
                this.loading.set(false);
            },
            error: () => this.loading.set(false)
        });
    }

    saveProfile() {
        if (this.profileForm.invalid) return;
        this.savingProfile.set(true);
        this.profileError.set('');

        this.profileService.updateProfile(this.profileForm.value as any).subscribe({
            next: (data) => {
                this.profile.set(data);
                this.savingProfile.set(false);
                this.profileSuccess.set(true);
                setTimeout(() => this.profileSuccess.set(false), 3000);
            },
            error: (err) => {
                this.savingProfile.set(false);
                this.profileError.set(err.error?.message ?? 'Error al guardar los cambios');
            }
        });
    }

    savePassword() {
        if (this.passwordForm.invalid) return;
        this.savingPassword.set(true);
        this.passwordError.set('');

        this.profileService.changePassword(this.passwordForm.value as any).subscribe({
            next: () => {
                this.passwordForm.reset();
                this.savingPassword.set(false);
                this.passwordSuccess.set(true);
                setTimeout(() => this.passwordSuccess.set(false), 3000);
            },
            error: (err) => {
                this.savingPassword.set(false);
                this.passwordError.set(err.error?.message ?? 'Error al cambiar la contraseña');
            }
        });
    }

    getRoleLabel(role: string): string {
        const map: Record<string, string> = {
            'ROLE_ADMIN': 'Administrador',
            'ROLE_ANALYST': 'Analista'
        };
        return map[role] ?? role;
    }

    getInitials(p: ProfileResponse): string {
        return `${p.firstname?.charAt(0) ?? ''}${p.firstLastname?.charAt(0) ?? ''}`.toUpperCase();
    }

    private passwordMatchValidator(control: AbstractControl) {
        const newPwd = control.get('newPassword')?.value;
        const confirmPwd = control.get('confirmPassword')?.value;
        return newPwd === confirmPwd ? null : {passwordMismatch: true};
    }
}
