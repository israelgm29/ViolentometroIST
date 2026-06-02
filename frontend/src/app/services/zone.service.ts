import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { ViolenceZoneInterface } from '../models/zone';

@Injectable({ providedIn: 'root' })
export class ZoneService {
  private http   = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/v1/violence-zones`;

  getAllZones(): Observable<ViolenceZoneInterface[]> {
    return this.http.get<ViolenceZoneInterface[]>(this.apiUrl);
  }

  getPlantillas(): Observable<ViolenceZoneInterface[]> {
    return this.http.get<ViolenceZoneInterface[]>(`${this.apiUrl}/plantillas`);
  }

  createZone(zone: ViolenceZoneInterface): Observable<ViolenceZoneInterface> {
    return this.http.post<ViolenceZoneInterface>(this.apiUrl, zone);
  }

  updateZone(id: number, zone: ViolenceZoneInterface): Observable<ViolenceZoneInterface> {
    return this.http.put<ViolenceZoneInterface>(`${this.apiUrl}/${id}`, zone);
  }

  deleteZone(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}