import {Component, EventEmitter, Output} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {RouterLink, RouterLinkActive} from "@angular/router";



@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    MatIcon,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  // Emite hacia el layout para que cierre el mat-drawer
  @Output() closeRequested = new EventEmitter<void>();

  closeSidebar(): void {
    this.closeRequested.emit();
  }
}
