// admin-layout.component.ts
import { Component, HostBinding, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { RouterOutlet }          from '@angular/router';
import { MatDrawer, MatSidenavModule } from '@angular/material/sidenav';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { skip } from 'rxjs/operators';
import { AuthService }        from '../../services/auth.service';
import { InactivityService }  from '../../services/inactivity.service';
import {SidebarAdmin} from "../../shared/admin/sidebar/sidebar";
import {Navbar} from "../../shared/admin/navbar/navbar";


@Component({
    selector:    'app-admin-layout',
    standalone:  true,
    imports:     [RouterOutlet, MatSidenavModule, Navbar],
    templateUrl: './admin-layout.component.html',
    styleUrl:    './admin-layout.component.scss'
})
export class AdminLayoutComponent implements OnInit, OnDestroy {
    @ViewChild('drawer') drawer!: MatDrawer;

    isLargeScreen = false;

    @HostBinding('attr.data-theme') theme = 'admin';

    private inactivityService = inject(InactivityService);
    private authService       = inject(AuthService);

    constructor(private breakpointObserver: BreakpointObserver) {}

    ngOnInit() {
        this.inactivityService.start();
        this.breakpointObserver
            .observe([Breakpoints.Large, Breakpoints.XLarge])
            .pipe(skip(1))
            .subscribe(result => {
                this.isLargeScreen = result.matches;
            });
    }

    ngOnDestroy() {
        this.inactivityService.stop();
    }

    logout(): void {
        this.authService.logout();
    }
}