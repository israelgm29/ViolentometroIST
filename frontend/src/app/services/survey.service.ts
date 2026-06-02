import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { ActiveSurveyDTO, FullSurveyDTO, Survey } from '../models/survey';

@Injectable({ providedIn: 'root' })
export class SurveyService {
  private http   = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/v1/surveys`;

  getAllSurveys(): Observable<Survey[]> {
    return this.http.get<Survey[]>(this.apiUrl);
  }

  getSurveyById(id: number): Observable<FullSurveyDTO> {
    return this.http.get<FullSurveyDTO>(`${this.apiUrl}/${id}`);
  }

  saveFullSurvey(data: FullSurveyDTO): Observable<FullSurveyDTO> {
    if (data.id) {
      return this.http.put<FullSurveyDTO>(`${this.apiUrl}/full/${data.id}`, data);
    }
    return this.http.post<FullSurveyDTO>(`${this.apiUrl}/full`, data);
  }

  activateSurvey(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/activate`, {});
  }

  deleteSurvey(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getActiveSurvey(): Observable<ActiveSurveyDTO> {
    return this.http.get<ActiveSurveyDTO>(`${this.apiUrl}/active`);
  }
}