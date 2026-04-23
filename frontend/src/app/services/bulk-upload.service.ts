import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface BulkUploadResult {
    totalRows: number;
    created:   number;
    updated:   number;
    errors:    number;
    rowErrors: RowError[];
}

export interface RowError {
    row:    number;
    dni:    string;
    reason: string;
}

@Injectable({ providedIn: 'root' })
export class BulkUploadService {
    private http   = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/v1/app-users/bulk`;

    upload(file: File): Observable<BulkUploadResult> {
        const formData = new FormData();
        formData.append('file', file);
        return this.http.post<BulkUploadResult>(
            `${this.apiUrl}/upload`,
            formData,
            { withCredentials: true }
        );
    }
}