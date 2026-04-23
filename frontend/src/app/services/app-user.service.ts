import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Observable, tap, catchError, of, throwError} from "rxjs";
import {environment} from "../../environments/environment";
import {AppUserRequest, AppUserResponse} from "../models/app-user";

@Injectable({
    providedIn: 'root',
})
export class AppUserService {

    private http: HttpClient;
    private apiUrl = `${environment.apiUrl}/v1/app-users`;

    constructor(http: HttpClient) {
        this.http = http;
    }

    createAppUser(appUser: AppUserRequest): Observable<AppUserResponse> {
        return this.http.post<AppUserResponse>(this.apiUrl, appUser);
    }

    getAppUsers(): Observable<AppUserResponse[]> {
        return this.http.get<AppUserResponse[]>(this.apiUrl);
    }

    getAppUserByDni(dni: string): Observable<AppUserResponse | null> {
        return this.http.get<AppUserResponse>(`${this.apiUrl}/${dni}`).pipe(
            catchError((error: HttpErrorResponse) => {
                if (error.status === 404) {
                    return of(null);
                }
                return throwError(() => error);
            })
        );
    }

    updateAppUser(dni: string, appUser: AppUserRequest): Observable<AppUserRequest> {
        return this.http.put<AppUserRequest>(`${this.apiUrl}/${dni}`, appUser);
    }

    patchStatus(dni: string, status: boolean): Observable<AppUserResponse> {
        return this.http.patch<AppUserResponse>(`${this.apiUrl}/${dni}/status?status=${status}`, null);
    }

    deleteAppUser(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }

    validateAndLogUser(cedula: string): Observable<AppUserResponse | null> {
        return this.getAppUserByDni(cedula)
    }

    isProfileComplete(user: AppUserResponse): boolean {
        return !!(
            user.gender?.id &&
            user.birthdate &&
            user.region?.id &&
            user.ethnicity?.id &&
            user.institute?.id
        );
    }

}