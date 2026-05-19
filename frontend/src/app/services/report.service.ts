import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';

export interface ReportRequest {
    startDate: string;
    endDate:   string;
    surveyId?: number;
}

@Injectable({
    providedIn: 'root',
})
export class ReportService {
    private http   = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/v1/reports`;

    downloadCriticalCasesPdf(data: ReportRequest): Observable<Blob> {
        return this.http.post(
            `${this.apiUrl}/critical-cases/pdf`,
            data,
            { responseType: 'blob' }
        );
    }

    downloadCriticalCasesExcel(data: ReportRequest): Observable<Blob> {
        return this.http.post(
            `${this.apiUrl}/critical-cases/excel`,
            data,
            { responseType: 'blob' }
        );
    }

    downloadGeneralReportPdf(body: { startDate: string; endDate: string; surveyId: number }): Observable<Blob> {
        return this.http.post(
            `${this.apiUrl}/general/pdf`,
            body,
            { responseType: 'blob' }
        );
    }

    downloadStudentTrackingExcel(body: {
        dni: string;
        startDate: string;
        endDate: string;
        surveyId: number;
    }): Observable<Blob> {
        return this.http.post(
            `${this.apiUrl}/student-tracking/excel`,
            body,
            { responseType: 'blob' }
        );
    }

    downloadPeriodComparisonExcel(body: {
        startDate1: string;
        endDate1:   string;
        startDate2: string;
        endDate2:   string;
        surveyId:   number;
    }): Observable<Blob> {
        return this.http.post(
            `${this.apiUrl}/period-comparison/excel`,
            body,
            { responseType: 'blob' }
        );
    }

    downloadParticipationExcel(body: { startDate: string; endDate: string; surveyId: number }): Observable<Blob> {
        return this.http.post(
            `${this.apiUrl}/participation/excel`,
            body,
            { responseType: 'blob' }
        );
    }


}