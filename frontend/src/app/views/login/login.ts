import { Component, signal }       from '@angular/core';
import { CommonModule }             from '@angular/common';
import { FormBuilder, FormGroup,
  ReactiveFormsModule,
  Validators }               from '@angular/forms';
import { Router }                   from '@angular/router';
import { MatCardModule }            from '@angular/material/card';
import { MatFormFieldModule }       from '@angular/material/form-field';
import { MatInputModule }           from '@angular/material/input';
import { MatButtonModule }          from '@angular/material/button';
import { MatIconModule }            from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService }              from '../../services/auth.service';
import { finalize }                 from 'rxjs';

@Component({
  selector:    'app-login',
  standalone:  true,
  imports: [
    CommonModule, ReactiveFormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatIconModule, MatProgressSpinnerModule,
  ],
  templateUrl: './login.html',
  styleUrls:   ['./login.scss']
})
export class Login {

  loginForm:   FormGroup;
  hidePassword = true;
  loading      = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(
      private fb:     FormBuilder,
      private router: Router,
      private auth:   AuthService,
  ) {
    // Si ya hay sesión activa, redirige directo
    if (this.auth.isLoggedIn()) {
      this.router.navigate(['/admin/dashboard']);
    }

    this.loginForm = this.fb.group({
      email:    ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(2)]],
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid || this.loading()) return;

    this.loading.set(true);
    this.errorMessage.set(null);

    this.auth.login(this.loginForm.value)
        .pipe(finalize(() => this.loading.set(false)))
        .subscribe({
          next: () => {
            // Cargar sesión completa para obtener datos del usuario
            this.auth.loadSession().subscribe({
              next: () => this.router.navigate(['/admin/dashboard']),
              error: () => this.router.navigate(['/admin/dashboard'])
            });
          },
          error: (err) => {
            if (err.status === 401 || err.status === 403) {
              this.errorMessage.set('Credenciales incorrectas. Verifica tu email y contraseña.');
            } else if (err.status === 0) {
              this.errorMessage.set('No se pudo conectar con el servidor.');
            } else {
              this.errorMessage.set('Error inesperado. Intenta nuevamente.');
            }
          }
        });
  }

  getEmailErrorMessage(): string {
    const c = this.loginForm.get('email');
    if (c?.hasError('required')) return 'El email es requerido';
    if (c?.hasError('email'))    return 'Ingresa un email válido';
    return '';
  }

  getPasswordErrorMessage(): string {
    const c = this.loginForm.get('password');
    if (c?.hasError('required'))  return 'La contraseña es requerida';
    if (c?.hasError('minlength')) return 'Mínimo 6 caracteres';
    return '';
  }
}