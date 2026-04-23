import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { UserAnswerDTO, UserAnswerRequest, UserAnswerResponse } from "../models/user-answer";

export interface CanAnswerTodayResponse {
    canAnswer: boolean;
    message:   string | null;
}

@Injectable({ providedIn: 'root' })
export class UserAnswerService {

    private http   = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/v1/user-answers`;

    saveAnswer(data: UserAnswerRequest): Observable<UserAnswerResponse> {
        return this.http.post<UserAnswerResponse>(this.apiUrl, data);
    }

    saveAnswersBatch(answers: UserAnswerRequest[]): Observable<UserAnswerResponse[]> {
        return this.http.post<UserAnswerResponse[]>(`${this.apiUrl}/batch`, answers);
    }

    getByDni(encryptedDni: string): Observable<UserAnswerDTO[]> {
        return this.http.get<UserAnswerDTO[]>(
            `${this.apiUrl}/user/dni/${encryptedDni}`
        ).pipe(catchError(() => of([])));
    }

    /** Respuestas históricas — no usar en el flujo del quiz */
    getByDniAndSurvey(encryptedDni: string, surveyId: number): Observable<UserAnswerDTO[]> {
        return this.http.get<UserAnswerDTO[]>(
            `${this.apiUrl}/user/dni/${encryptedDni}/survey/${surveyId}`
        ).pipe(catchError(err => {
            console.error('Error getByDniAndSurvey:', err);
            return of([]);
        }));
    }

    /**
     * ✅ Usar este en loadQuestionsAndResume().
     * Solo devuelve respuestas del día de hoy — evita que cargue
     * respuestas de días anteriores y salte al final del cuestionario.
     */
    getByDniAndSurveyToday(encryptedDni: string, surveyId: number | undefined): Observable<UserAnswerDTO[]> {
        return this.http.get<UserAnswerDTO[]>(
            `${this.apiUrl}/user/dni/${encryptedDni}/survey/${surveyId}/today`
        ).pipe(catchError(err => {
            console.error('Error getByDniAndSurveyToday:', err);
            return of([]);
        }));
    }

    /**
     * Verifica si el usuario puede responder el cuestionario hoy.
     * Si el backend falla, permite el acceso para no bloquear al estudiante.
     */
    canAnswerToday(userId: number, surveyId: number): Observable<CanAnswerTodayResponse> {
        return this.http.get<CanAnswerTodayResponse>(
            `${this.apiUrl}/can-answer`,
            { params: { userId: userId.toString(), surveyId: surveyId.toString() } }
        ).pipe(
            catchError(() => of({ canAnswer: true, message: null }))
        );
    }
}