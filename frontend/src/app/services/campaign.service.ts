import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {CampaignCategory, CampaignDTO} from "../models/campañing";

@Injectable({providedIn: 'root'})
export class CampaignService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/v1/campaigns`;

    // ── PÚBLICO ───────────────────────────────────────────────
    getActive(): Observable<CampaignDTO[]> {
        return this.http.get<CampaignDTO[]>(`${this.apiUrl}/active`);
    }

    getActiveCategories(): Observable<CampaignCategory[]> {
        return this.http.get<CampaignCategory[]>(`${this.apiUrl}/categories/active`);
    }

    // ── ADMIN ─────────────────────────────────────────────────
    getAll(): Observable<CampaignDTO[]> {
        return this.http.get<CampaignDTO[]>(this.apiUrl, {withCredentials: true});
    }

    getById(id: number): Observable<CampaignDTO> {
        return this.http.get<CampaignDTO>(`${this.apiUrl}/${id}`, {withCredentials: true});
    }

    create(dto: Omit<CampaignDTO, 'id'>): Observable<CampaignDTO> {
        return this.http.post<CampaignDTO>(this.apiUrl, dto, {withCredentials: true});
    }

    update(id: number, dto: CampaignDTO): Observable<CampaignDTO> {
        return this.http.put<CampaignDTO>(`${this.apiUrl}/${id}`, dto, {withCredentials: true});
    }

    toggleStatus(id: number): Observable<void> {
        return this.http.patch<void>(`${this.apiUrl}/${id}/toggle`, {}, {withCredentials: true});
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`, {withCredentials: true});
    }

    // ── CATEGORÍAS ────────────────────────────────────────────
    getAllCategories(): Observable<CampaignCategory[]> {
        return this.http.get<CampaignCategory[]>(`${this.apiUrl}/categories`, {withCredentials: true});
    }

    createCategory(dto: Partial<CampaignCategory>): Observable<CampaignCategory> {
        return this.http.post<CampaignCategory>(`${this.apiUrl}/categories`, dto, {withCredentials: true});
    }

    updateCategory(id: number, dto: Partial<CampaignCategory>): Observable<CampaignCategory> {
        return this.http.put<CampaignCategory>(`${this.apiUrl}/categories/${id}`, dto, {withCredentials: true});
    }
}