import { Component, EventEmitter, inject, Output } from '@angular/core';
import { NgOptimizedImage } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatDivider } from '@angular/material/list';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-navbar-admin',
  standalone: true,
  imports: [
    MatIcon,
    MatMenuTrigger,
    MatMenu,
    MatMenuItem,
    MatDivider,
    RouterLink,
    RouterLinkActive,
    NgOptimizedImage,
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class Navbar {
  @Output() logoutClick = new EventEmitter<void>();

  public authService = inject(AuthService);

  get currentUser() {
    return this.authService.user();
  }

  logout(): void {
    this.logoutClick.emit();
  }
}