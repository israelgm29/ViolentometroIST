import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {MasterCatalog} from "../models/app-user";

@Injectable({
  providedIn: 'root',
})
export class CatalogService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/v1`;

  // Listar todos según el recurso (path)
  findAll(path: string): Observable<MasterCatalog[]> {
    return this.http.get<MasterCatalog[]>(`${this.apiUrl}/${path}`);
  }

  // Crear nuevo registro
  save(path: string, data: MasterCatalog): Observable<MasterCatalog> {
    return this.http.post<MasterCatalog>(`${this.apiUrl}/${path}`, data);
  }

  // Actualizar registro existente
  update(path: string, id: number, data: MasterCatalog): Observable<MasterCatalog> {
    return this.http.put<MasterCatalog>(`${this.apiUrl}/${path}/${id}`, data);
  }

  // Eliminar
  delete(path: string, id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${path}/${id}`);
  }

}
