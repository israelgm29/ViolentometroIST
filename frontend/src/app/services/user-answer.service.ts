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

    getByDni(dni: string): Observable<UserAnswerDTO[]> {
        return this.http.get<UserAnswerDTO[]>(
            `${this.apiUrl}/user/dni/${dni}`
        ).pipe(catchError(() => of([])));
    }

    getByDniAndSurvey(dni: string, surveyId: number): Observable<UserAnswerDTO[]> {
        return this.http.get<UserAnswerDTO[]>(
            `${this.apiUrl}/user/dni/${dni}/survey/${surveyId}`
        ).pipe(catchError(err => {
            console.error('Error getByDniAndSurvey:', err);
            return of([]);
        }));
    }

    // FIX: recibe DNI plano — encrypt() no funciona sin HTTPS
    getByDniAndSurveyToday(dni: string, surveyId: number): Observable<UserAnswerResponse[]> {
        return this.http.get<UserAnswerResponse[]>(
            `${this.apiUrl}/user/dni/${dni}/survey/${surveyId}/today`
        ).pipe(catchError(() => of([])));
    }

    canAnswerToday(userId: number, surveyId: number): Observable<CanAnswerTodayResponse> {
        return this.http.get<CanAnswerTodayResponse>(
            `${this.apiUrl}/can-answer`,
            { params: { userId: userId.toString(), surveyId: surveyId.toString() } }
        ).pipe(
            catchError(() => of({ canAnswer: true, message: null }))
        );
    }
}