import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { catchError, map, of } from 'rxjs';
import { environment } from '../../environments/environment';

interface CanAnswerResponse {
    canAnswer: boolean;
    message:   string | null;
}

/**
 * Guard que verifica si el estudiante ya respondió el cuestionario hoy.
 *
 * Uso en rutas:
 *   {
 *     path: 'survey/:surveyId',
 *     component: SurveyComponent,
 *     canActivate: [surveyAccessGuard]
 *   }
 *
 * Requiere que el userId y surveyId estén disponibles.
 * Ajusta cómo obtienes el userId según tu sistema de autenticación.
 */
export const surveyAccessGuard: CanActivateFn = (route) => {
    const router = inject(Router);
    const http   = inject(HttpClient);

    const surveyId = route.paramMap.get('surveyId');
    // Ajusta esto según dónde guardas el userId en tu app
    // (localStorage, signal de AuthService, etc.)
    const userId = localStorage.getItem('userId');

    if (!userId || !surveyId) {
        router.navigate(['/']);
        return of(false);
    }

    const url = `${environment.apiUrl}/v1/user-answers/can-answer`;

    return http.get<CanAnswerResponse>(url, {
        params: { userId, surveyId }
    }).pipe(
        map(response => {
            if (response.canAnswer) {
                return true;
            }
            // Ya respondió hoy — redirige con el mensaje
            router.navigate(['/survey-blocked'], {
                queryParams: { message: response.message }
            });
            return false;
        }),
        catchError(() => {
            // Si el backend falla, permitimos el acceso para no bloquear al estudiante
            return of(true);
        })
    );
};