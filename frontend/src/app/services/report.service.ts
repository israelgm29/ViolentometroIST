import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

export interface ReportRequest {
    startDate: string;
    endDate: string;
    surveyId?: number;
}

@Injectable({
    providedIn: 'root',
})
export class ReportService {

    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/v1/reports`;

    downloadCriticalCasesPdf(data: ReportRequest): Observable<Blob> {
        return this.http.post(
            `${this.apiUrl}/critical-cases/pdf`,
            data,
            {
                responseType: 'blob'
            }
        );
    }
}