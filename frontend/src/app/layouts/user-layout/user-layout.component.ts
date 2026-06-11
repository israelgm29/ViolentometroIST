import {
  Component,
  ViewChild,
  inject
} from '@angular/core';

import { RouterOutlet } from '@angular/router';

import { SidebarComponent } from '../../shared/sidebar/sidebar.component';

import {
  MatDrawer,
  MatDrawerContainer,
  MatDrawerContent
} from '@angular/material/sidenav';

import { MatIcon } from '@angular/material/icon';

import {
  BreakpointObserver,
  Breakpoints
} from '@angular/cdk/layout';

import { map } from 'rxjs/operators';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-user-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    MatDrawerContainer,
    MatDrawer,
    MatDrawerContent,
    MatIcon,
    SidebarComponent
  ],
  templateUrl: './user-layout.component.html',
  styleUrl: './user-layout.component.scss'
})
export class UserLayoutComponent {

  private breakpointObserver = inject(BreakpointObserver);

  @ViewChild('drawer')
  drawer!: MatDrawer;

  readonly isMobile = toSignal(
      this.breakpointObserver
          .observe([
            Breakpoints.Handset,
            Breakpoints.TabletPortrait,
            '(max-width: 768px)'
          ])
          .pipe(
              map(result => result.matches)
          ),
      {
        initialValue: false
      }
  );

  toggleDrawer(): void {
    this.drawer?.toggle();
  }
}