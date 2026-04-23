import { HttpInterceptorFn } from '@angular/common/http';
import { inject }            from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { Router }            from '@angular/router';
import { AuthService }       from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const auth   = inject(AuthService);
    const router = inject(Router);

    // ← Solo agregamos withCredentials para que el navegador envíe la cookie
    const authReq = req.clone({ withCredentials: true });

    return next(authReq).pipe(
        catchError(err => {
            if (err.status === 401) {
                auth.logout();
                router.navigate(['/admin/login']);
            }
            return throwError(() => err);
        })
    );
};