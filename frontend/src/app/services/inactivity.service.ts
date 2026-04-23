import { Injectable, inject, NgZone } from '@angular/core';
import { Router }       from '@angular/router';
import { AuthService }  from './auth.service';
import { MatDialog }    from '@angular/material/dialog';
import { InactivityWarningDialogComponent } from '../components/inactivity-warning-dialog/inactivity-warning-dialog';

@Injectable({ providedIn: 'root' })
export class InactivityService {

    private router     = inject(Router);
    private authService = inject(AuthService);
    private dialog     = inject(MatDialog);
    private ngZone     = inject(NgZone);

    private readonly INACTIVITY_TIME = 27 * 60 * 1000; // 27 min → muestra advertencia
    private readonly WARNING_TIME    =  3 * 60 * 1000; //  3 min → cierra sesión
    private inactivityTimer: any;
    private warningTimer:    any;
    private warningOpen = false;

    // Eventos que se consideran actividad
    private readonly ACTIVITY_EVENTS = [
        'mousemove', 'mousedown', 'keydown',
        'scroll', 'touchstart', 'click'
    ];

    start() {
        this.ACTIVITY_EVENTS.forEach(event =>
            window.addEventListener(event, () => this.resetTimer(), { passive: true })
        );
        this.resetTimer();
    }

    stop() {
        this.ACTIVITY_EVENTS.forEach(event =>
            window.removeEventListener(event, () => this.resetTimer())
        );
        clearTimeout(this.inactivityTimer);
        clearTimeout(this.warningTimer);
    }

    private resetTimer() {
        // Si hay advertencia abierta y el usuario hace algo → cerrarla
        if (this.warningOpen) {
            this.dialog.closeAll();
            this.warningOpen = false;
        }

        clearTimeout(this.inactivityTimer);
        clearTimeout(this.warningTimer);

        // Fuera de la zona de Angular para no disparar change detection
        this.ngZone.runOutsideAngular(() => {
            this.inactivityTimer = setTimeout(() => {
                this.ngZone.run(() => this.showWarning());
            }, this.INACTIVITY_TIME);
        });
    }

    private showWarning() {
        this.warningOpen = true;

        const dialogRef = this.dialog.open(InactivityWarningDialogComponent, {
            width:        '420px',
            disableClose: true,
            data:         { seconds: this.WARNING_TIME / 1000 }
        });

        // Si el usuario hace clic en "Continuar" → reinicia
        dialogRef.afterClosed().subscribe(keepSession => {
            this.warningOpen = false;
            if (keepSession) {
                this.resetTimer();
            } else {
                this.logout();
            }
        });

        // Timer de cierre automático si no responde
        this.ngZone.runOutsideAngular(() => {
            this.warningTimer = setTimeout(() => {
                this.ngZone.run(() => {
                    this.dialog.closeAll();
                    this.logout();
                });
            }, this.WARNING_TIME);
        });
    }

    private logout() {
        this.stop();
        this.authService.logout();
    }
}