import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {ActiveSurveyDTO, FullSurveyDTO, Survey} from "../models/survey";


@Injectable({
  providedIn: 'root',
})
export class SurveyService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/v1/surveys`;

  // ========== LISTAR TODOS ==========
  getAllSurveys(): Observable<Survey[]> {
    return this.http.get<Survey[]>(this.apiUrl);
  }

  // ========== OBTENER UNO POR ID ==========
  getSurveyById(id: number): Observable<FullSurveyDTO> {
    return this.http.get<FullSurveyDTO>(`${this.apiUrl}/${id}`);
  }

  // ========== GUARDAR (CREAR O ACTUALIZAR) ==========
  saveFullSurvey(data: FullSurveyDTO): Observable<FullSurveyDTO> {
    if (data.id) {
      // Si tiene ID, es actualización
      return this.http.put<FullSurveyDTO>(`${this.apiUrl}/full/${data.id}`, data);
    }
    // Si no tiene ID, es creación
    return this.http.post<FullSurveyDTO>(`${this.apiUrl}/full`, data);
  }

  // ========== ACTIVAR ==========
  activateSurvey(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/activate`, {});
  }

  // ========== ELIMINAR ==========
  deleteSurvey(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getActiveSurvey(): Observable<ActiveSurveyDTO> {  // ← ActiveSurveyDTO no FullSurveyDTO
    return this.http.get<ActiveSurveyDTO>(`${this.apiUrl}/active`);
  }
}