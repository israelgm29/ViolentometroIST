import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient }  from '@angular/common/http';
import { Router }      from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

export interface LoginRequest {
    email:    string;
    password: string;
}

export interface LoginResponse {
    id:       number;
    username: string;
    email:    string;
    role:     string;
}

export interface AuthUser extends LoginResponse {}

@Injectable({ providedIn: 'root' })
export class AuthService {
    private http   = inject(HttpClient);
    private router = inject(Router);
    private readonly API = `${environment.apiUrl}/v1/auth`;

    // ← Ya no cargamos nada desde localStorage
    private _user = signal<AuthUser | null>(null);

    readonly user       = this._user.asReadonly();
    readonly isLoggedIn = computed(() => this._user() !== null);
    readonly role       = computed(() => this._user()?.role ?? null);
    readonly isAdmin    = computed(() => this._user()?.role === 'ROLE_ADMIN');
    readonly isAnalyst  = computed(() => this._user()?.role === 'ROLE_ANALYST');
    readonly isWelfare  = computed(() => this._user()?.role === 'ROLE_WELFARE');

    // ── Login ────────────────────────────────────
    login(credentials: LoginRequest): Observable<LoginResponse> {
        return this.http
            .post<LoginResponse>(`${this.API}/login`, credentials, {
                withCredentials: true  // ← permite enviar/recibir cookies
            })
            .pipe(
                tap(response => this._user.set(response))
            );
    }

    // ── Logout ───────────────────────────────────
    logout(): void {
        this.http.post(`${this.API}/logout`, {}, { withCredentials: true })
            .subscribe({
                complete: () => {
                    this._user.set(null);
                    this.router.navigate(['/admin/login']);
                }
            });
    }

    // ── Verifica sesión activa llamando al backend ────────
    // El navegador envía la cookie automáticamente
    loadSession(): Observable<AuthUser> {
        return this.http
            .get<AuthUser>(`${this.API}/me`, { withCredentials: true })
            .pipe(
                tap(user => this._user.set(user))
            );
    }

    // ── Verificación de rol ──────────────────────
    hasRole(role: string): boolean {
        return this._user()?.role === role;
    }

    hasAnyRole(...roles: string[]): boolean {
        return roles.some(r => this.hasRole(r));
    }
}