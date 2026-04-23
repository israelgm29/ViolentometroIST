import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {SysUserInterface} from "../models/sys-user";

@Injectable({
  providedIn: 'root',
})
export class SysUserService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/v1/sys-users`;

    // Add methods for sys-user operations here
  getSysUsers():Observable<SysUserInterface[]> {
    return this.http.get<SysUserInterface[]>(`${this.apiUrl}`);
  }

  saveUser(user: SysUserInterface):Observable<SysUserInterface> {
    return this.http.post<SysUserInterface>(`${this.apiUrl}`, user);
  }

  updateSysUser(id: number, user: SysUserInterface):Observable<SysUserInterface> {
    return this.http.put<SysUserInterface>(`${this.apiUrl}/${id}`, user);
  }

  patchStatus(id: number, status: boolean): Observable<SysUserInterface> {
    return this.http.patch<SysUserInterface>(`${this.apiUrl}/${id}/status?status=${status}`, null);
  }

  resetPassword(id: number, newPassword: string): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/reset-password`, { newPassword });
  }
  
}
