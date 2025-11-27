import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Violentometro } from '../../features/violentometro/models/violentometro.model';
import { Persona } from '../../features/violentometro/models/persona.model';
import { Instituto } from '../../features/violentometro/models/instituto.model';
import { PreguntaFija } from '../../features/violentometro/models/pregunta-fija.model';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api';

  private headers = new HttpHeaders({ 'Content-Type': 'application/json' });

  getPersonaByCedula(cedula: string): Observable<Persona> {
    return this.http.get<Persona>(`${this.baseUrl}/personas/${cedula}`, { headers: this.headers });
  }

  getInstitutos(): Observable<Instituto[]> {
    return this.http.get<Instituto[]>(`${this.baseUrl}/institutos`, { headers: this.headers });
  }

  getPreguntasFijas(): Observable<PreguntaFija[]> {
    return this.http.get<PreguntaFija[]>(`${this.baseUrl}/preguntas-fijas`, { headers: this.headers });
  }

  saveViolentometro(dto: Violentometro): Observable<Violentometro> {
    return this.http.post<Violentometro>(`${this.baseUrl}/violentometros`, dto, { headers: this.headers });
  }
}
