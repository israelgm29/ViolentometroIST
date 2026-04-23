import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {DashboardData, ReportDetailDTO, TrendResponseDTO} from "../models/statistics";
import {CriticalCase, CriticalCasesReport} from "../models/case-reports";

@Injectable({
    providedIn: 'root',
})
export class StatisticsService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/v1/statistics`;

    getDashboardData(startDate?: string, endDate?: string, surveyId?: number): Observable<DashboardData> {
        let params = new HttpParams();
        if (startDate) params = params.set('startDate', startDate);
        if (endDate) params = params.set('endDate', endDate);
        if (surveyId) params = params.set('surveyId', surveyId.toString());

        return this.http.get<DashboardData>(this.apiUrl, {params});
    }

    getDetailedReport(type: string, start: string, end: string, surveyId?: number): Observable<ReportDetailDTO[]> {
        let params = new HttpParams()
            .set('type', type)
            .set('start', start)
            .set('end', end);

        if (surveyId) params = params.set('surveyId', surveyId.toString());

        return this.http.get<ReportDetailDTO[]>(`${this.apiUrl}/detailed-report`, {params});
    }

    getCriticalCasesReport(startDate: string, endDate: string, surveyId: number): Observable<CriticalCasesReport> {
        let params = new HttpParams()
            .set('startDate', startDate)
            .set('endDate', endDate)
            .set('surveyId', surveyId.toString());

        return this.http.get<CriticalCasesReport>(`${this.apiUrl}/critical-cases`, {params});
    }

    getTrends(start: string, end: string, surveyId: number): Observable<TrendResponseDTO> {
        let params = new HttpParams()
            .set('start', start)
            .set('end', end)
            .set('surveyId', surveyId.toString());

        return this.http.get<TrendResponseDTO>(`${this.apiUrl}/trends`, {params});
    }
}