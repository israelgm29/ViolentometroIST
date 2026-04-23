import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";
import {SysRoleInterface} from "../models/sys-role";

@Injectable({
  providedIn: 'root',
})
export class SysRoleService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/v1/roles`;

  getSysRoles(): Observable<SysRoleInterface[]> {
    return this.http.get<SysRoleInterface[]>(`${this.apiUrl}`);
  }
}
