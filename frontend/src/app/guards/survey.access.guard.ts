import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { catchError, map, of, switchMap } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from '../services/auth.service';

interface CanAnswerResponse {
    canAnswer: boolean;
    message:   string | null;
}

export const surveyAccessGuard: CanActivateFn = (route) => {
    const router      = inject(Router);
    const http        = inject(HttpClient);
    const authService = inject(AuthService);

    const surveyId = route.paramMap.get('surveyId');

    if (!surveyId) {
        router.navigate(['/']);
        return of(false);
    }

    const userId$ = authService.isLoggedIn()
        ? of(authService.user()!.id)
        : authService.loadSession().pipe(map(u => u.id));

    return userId$.pipe(
        switchMap(userId => {
            if (!userId) {
                router.navigate(['/']);
                return of(false);
            }

            return http.get<CanAnswerResponse>(
                `${environment.apiUrl}/v1/user-answers/can-answer`,
                { params: { userId: userId.toString(), surveyId } }
            ).pipe(
                map(response => {
                    if (response.canAnswer) return true;
                    router.navigate(['/survey-blocked'], {
                        queryParams: { message: response.message }
                    });
                    return false;
                }),
                catchError(() => of(true))
            );
        }),
        catchError(() => {
            router.navigate(['/']);
            return of(false);
        })
    );
};