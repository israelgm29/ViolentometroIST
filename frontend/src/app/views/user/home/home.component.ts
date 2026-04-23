import { Component, OnInit, OnDestroy, signal, inject } from '@angular/core';
import { ParticleBackgroundComponent } from '../../../shared/particle-background/particle-background.component';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { UserInfoModalComponent } from '../../../components/user-info-modal/user-info-modal.component';
import { Router, NavigationStart } from '@angular/router';
import { filter, switchMap, takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { AppUserService } from '../../../services/app-user.service';
import { ToastrService } from 'ngx-toastr';
import { QuestionsContainerComponent } from '../question-container/questions-container.component';
import { AppUserResponse } from '../../../models/app-user';
import { UserAnswerService } from '../../../services/user-answer.service';
import { SurveyService } from '../../../services/survey.service';

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [
        ParticleBackgroundComponent,
        MatDialogModule,
        MatIconModule,
        QuestionsContainerComponent
    ],
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {

    private appUserService    = inject(AppUserService);
    private userAnswerService = inject(UserAnswerService);
    private surveyService     = inject(SurveyService);
    private destroy$          = new Subject<void>();
    private dialogRef?        : MatDialogRef<UserInfoModalComponent>;

    showQuiz       = signal(false);
    currentUserId  = signal<number | null>(null);
    currentUserDni = signal<string | null>(null);
    // ✅ Pasamos el surveyId al componente hijo para evitar doble llamada
    currentSurveyId = signal<number | null>(null);

    constructor(
        private dialog:  MatDialog,
        private toaster: ToastrService,
        private router:  Router,
    ) {}

    ngOnInit() {
        this.openUserInfoModal();

        this.router.events.pipe(
            filter(event => event instanceof NavigationStart),
            takeUntil(this.destroy$)
        ).subscribe((event: any) => {
            if (event.url !== '/' && event.url !== '/home') {
                this.dialogRef?.close();
                this.showQuiz.set(false);
            }
        });
    }

    ngOnDestroy() {
        this.destroy$.next();
        this.destroy$.complete();
        this.dialogRef?.close();
    }

    openUserInfoModal() {
        this.dialogRef = this.dialog.open(UserInfoModalComponent, {
            width: '480px',
            maxWidth: '95vw',
            minWidth: '320px',
            panelClass: 'custom-modal-container',
            disableClose: true,
            autoFocus: false,
            hasBackdrop: true,
            backdropClass: 'custom-modal-backdrop'
        });

        this.dialogRef.afterClosed().subscribe(result => {
            if (!result) return;

            this.appUserService.validateAndLogUser(result.cedula).subscribe({
                next: (user: AppUserResponse | null) => {
                    if (!user) {
                        this.toaster.error(
                            'No se encontró un usuario con la cédula proporcionada.',
                            'Usuario Inválido'
                        );
                        this.openUserInfoModal();
                        return;
                    }
                    this.checkDailyLimit(user);
                },
                error: () => {
                    this.toaster.error(
                        'Hubo un error de conexión con el servidor. Intente más tarde.',
                        'Error de Servidor'
                    );
                }
            });
        });
    }

    private checkDailyLimit(user: AppUserResponse): void {
        // 1. Primero obtenemos el survey activo
        this.surveyService.getActiveSurvey().pipe(
            // 2. Con el surveyId, verificamos si puede responder hoy
            switchMap(survey =>
                this.userAnswerService.canAnswerToday(user.id, survey.id).pipe(
                    // Adjuntamos el survey para usarlo después
                    switchMap(response => {
                        if (!response.canAnswer) {
                            // ❌ Ya respondió hoy
                            this.toaster.warning(
                                response.message ?? 'Ya respondiste el cuestionario hoy. Vuelve mañana.',
                                '⏳ Límite diario alcanzado',
                                { timeOut: 6000, progressBar: true, closeButton: true }
                            );
                            setTimeout(() => this.openUserInfoModal(), 1000);
                            // Retorna observable vacío para no continuar
                            return new Subject<never>().asObservable();
                        }
                        // ✅ Puede responder — guardamos el surveyId y mostramos quiz
                        this.currentSurveyId.set(survey.id);
                        this.currentUserId.set(user.id);
                        this.currentUserDni.set(user.dni);
                        // showQuiz se activa DESPUÉS de tener toda la info
                        this.showQuiz.set(true);
                        return new Subject<never>().asObservable();
                    })
                )
            )
        ).subscribe({
            error: () => {
                // Si falla la verificación, dejamos pasar
                this.currentUserId.set(user.id);
                this.currentUserDni.set(user.dni);
                this.showQuiz.set(true);
            }
        });
    }
}