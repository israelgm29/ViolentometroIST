import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import {Router, RouterLink} from "@angular/router";
import {MatIcon} from "@angular/material/icon";
import {MatToolbar} from "@angular/material/toolbar";
import {AuthService} from "../../../services/auth.service";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatDivider} from "@angular/material/list";
import {MatIconButton} from "@angular/material/button";

interface Notification {
  icon: string;
  iconColor: string;
  message: string;
}

@Component({
  selector: 'app-navbar-admin',
  imports: [
    MatIcon,
    MatToolbar,
    MatMenuTrigger,
    MatMenu,
    MatDivider,
    MatMenuItem,
    RouterLink,
    MatIconButton
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  @Input()  drawerOpened = false;
  @Output() menuToggle   = new EventEmitter<void>();
  @Output() logoutClick  = new EventEmitter<void>();

  private authService = inject(AuthService);

  get currentUser() {
    return this.authService.user();
  }

  toggle()  { this.menuToggle.emit();  }
  logout()  { this.logoutClick.emit(); }
}
