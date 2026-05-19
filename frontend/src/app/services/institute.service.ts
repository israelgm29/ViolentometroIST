import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { InterfaceInstitute } from '../models/institute';

@Injectable({
  providedIn: 'root',
})
export class InstituteService {
  private http   = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/v1/institutes`;

  getInstitutes(): Observable<InterfaceInstitute[]> {
    return this.http.get<InterfaceInstitute[]>(this.apiUrl);
  }

  saveInstitute(institute: InterfaceInstitute): Observable<InterfaceInstitute> {
    return this.http.post<InterfaceInstitute>(this.apiUrl, institute);
  }

  updateInstitute(id: number, institute: InterfaceInstitute): Observable<InterfaceInstitute> {
    return this.http.put<InterfaceInstitute>(`${this.apiUrl}/${id}`, institute);
  }

  deleteInstitute(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // ── Logo ──────────────────────────────────────────────────────────

  uploadLogo(id: number, file: File): Observable<void> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<void>(`${this.apiUrl}/${id}/logo`, formData);
  }

  deleteLogo(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}/logo`);
  }

  getLogoUrl(id: number): string {
    return `${this.apiUrl}/${id}/logo`;
  }
}