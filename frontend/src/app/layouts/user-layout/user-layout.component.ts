import {Component, OnInit, ViewChild} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {SidebarComponent} from '../../shared/sidebar/sidebar.component';
import {fadeAnimation} from '../../route-animations';
import {MatDrawer, MatDrawerContainer, MatDrawerContent} from "@angular/material/sidenav";
import {MatIcon} from "@angular/material/icon";
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-user-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    SidebarComponent,
    MatDrawerContainer,
    MatDrawer,
    MatDrawerContent,
    MatIcon
  ],
  templateUrl: './user-layout.component.html',
  styleUrl: './user-layout.component.scss',
  animations: [fadeAnimation]
})
export class UserLayoutComponent implements OnInit {

  @ViewChild('drawer') drawer!: MatDrawer;

  isMobile = false;

  constructor(private breakpointObserver: BreakpointObserver) {}

  ngOnInit() {
    this.breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.TabletPortrait, '(max-width: 768px)'])
        .subscribe(result => {
          this.isMobile = result.matches;

          // En móvil: cerramos el drawer automáticamente
          if (this.isMobile) {
            this.drawer?.close();
          } else {
            this.drawer?.open();
          }
        });
  }

  prepareRoute(outlet: RouterOutlet) {
    return outlet?.activatedRouteData?.['animation'];
  }

  toggleDrawer() {
    this.drawer.toggle();
  }
}