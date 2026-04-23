import {inject} from '@angular/core';
import {CanActivateFn, Router} from '@angular/router';
import {AuthService} from '../services/auth.service';
import {map, catchError, of} from 'rxjs';

// ── Guard 1: Solo requiere estar autenticado ──────────────────────────────────
export const authGuard: CanActivateFn = () => {
    const auth = inject(AuthService);
    const router = inject(Router);

    // Si ya tenemos el usuario en memoria, no llamamos al backend
    if (auth.isLoggedIn()) return true;

    // Si no, verificamos con el backend si la cookie sigue válida
    return auth.loadSession().pipe(
        map(() => true),
        catchError(() => {
            router.navigate(['/admin/login']);
            return of(false);
        })
    );
};

// ── Guard 2: Requiere rol específico ──────────────────────────────────────────
export const roleGuard = (...requiredRoles: string[]): CanActivateFn => {
    return () => {
        const auth = inject(AuthService);
        const router = inject(Router);

        const check = () => {
            if (auth.hasAnyRole(...requiredRoles)) return true;
            // Redirigir según el rol que tenga
            if (auth.isWelfare()) {
                router.navigate(['/admin/dashboard']);
            } else if (auth.isAnalyst()) {
                router.navigate(['/admin/users']);
            } else {
                router.navigate(['/admin/dashboard']);
            }
            return false;
        };

        if (auth.isLoggedIn()) return check();

        return auth.loadSession().pipe(
            map(() => check()),
            catchError(() => {
                router.navigate(['/admin/login']);
                return of(false);
            })
        );
    };
};