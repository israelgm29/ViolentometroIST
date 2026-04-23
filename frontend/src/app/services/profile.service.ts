import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {ChangePasswordDTO, ProfileResponse, UpdateProfileDTO} from "../models/sys-user";

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  private http = inject(HttpClient);
  private API  = `${environment.apiUrl}/v1/profile`;

  getProfile(): Observable<ProfileResponse> {
    return this.http.get<ProfileResponse>(this.API, { withCredentials: true });
  }

  updateProfile(dto: UpdateProfileDTO): Observable<ProfileResponse> {
    return this.http.put<ProfileResponse>(this.API, dto, { withCredentials: true });
  }

  changePassword(dto: ChangePasswordDTO): Observable<void> {
    return this.http.patch<void>(`${this.API}/password`, dto, { withCredentials: true });
  }
}
