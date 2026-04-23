import { Component, EventEmitter, Output, inject } from '@angular/core';
import { MatListItem } from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import { RouterLink, RouterLinkActive } from '@angular/router';
import {AuthService} from "../../../services/auth.service";


@Component({
  selector: 'app-sidebar-admin',
  standalone: true,
  imports: [
    MatListItem,
    MatIconModule,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css'
})
export class SidebarAdmin {
  @Output() closeRequested = new EventEmitter<void>();

  // Inyectar AuthService para usar en el template
  authService = inject(AuthService);

  closeSidebar(): void {
    this.closeRequested.emit();
  }
}