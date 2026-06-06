import { Component, OnInit, OnDestroy, signal, inject } from '@angular/core';
import { Router, NavigationStart } from '@angular/router';

import { filter, switchMap, takeUntil } from 'rxjs/operators';
import { EMPTY, Subject } from 'rxjs';

import { ToastrService } from 'ngx-toastr';

import { QuestionsContainerComponent } from '../question-container/questions-container.component';

import { AppUserService } from '../../../services/app-user.service';
import { UserAnswerService } from '../../../services/user-answer.service';
import { SurveyService } from '../../../services/survey.service';

import { AppUserResponse } from '../../../models/app-user';
import {UserIdentificationComponent} from "../../../components/user-indentification/user-identification.component";

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [
        UserIdentificationComponent,
        QuestionsContainerComponent
    ],
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {

    private appUserService = inject(AppUserService);
    private userAnswerService = inject(UserAnswerService);
    private surveyService = inject(SurveyService);

    private destroy$ = new Subject<void>();

    showQuiz = signal(false);

    currentUserId = signal<number | null>(null);
    currentUserDni = signal<string | null>(null);
    currentSurveyId = signal<number | null>(null);

    constructor(
        private toaster: ToastrService,
        private router: Router
    ) {}

    ngOnInit(): void {

        this.router.events
            .pipe(
                filter(event => event instanceof NavigationStart),
                takeUntil(this.destroy$)
            )
            .subscribe((event: any) => {

                if (
                    event.url !== '/' &&
                    event.url !== '/home'
                ) {
                    this.showQuiz.set(false);
                }

            });

    }

    ngOnDestroy(): void {

        this.destroy$.next();
        this.destroy$.complete();

    }

    onUserValidated(event: {
        userId: number;
        cedula: string;
    }): void {

        this.appUserService
            .validateAndLogUser(event.cedula)
            .subscribe({

                next: (user: AppUserResponse | null) => {

                    if (!user) {

                        this.toaster.error(
                            'No se encontró un usuario válido.',
                            'Error'
                        );

                        return;
                    }

                    this.checkDailyLimit(user);
                },

                error: () => {

                    this.toaster.error(
                        'Error de conexión con el servidor.',
                        'Error'
                    );

                }

            });

    }

    onCancelIdentification(): void {

        window.location.reload();

    }

    private checkDailyLimit(
        user: AppUserResponse
    ): void {

        this.surveyService
            .getActiveSurvey()
            .pipe(

                switchMap(survey =>

                    this.userAnswerService
                        .canAnswerToday(user.id, survey.id)
                        .pipe(

                            switchMap(response => {

                                if (!response.canAnswer) {

                                    this.toaster.warning(
                                        response.message ??
                                        'Ya respondiste el cuestionario hoy. Vuelve mañana.',
                                        '⏳ Límite diario alcanzado',
                                        {
                                            timeOut: 6000,
                                            progressBar: true,
                                            closeButton: true
                                        }
                                    );

                                    this.showQuiz.set(false);

                                    return EMPTY;
                                }

                                this.currentSurveyId.set(survey.id);
                                this.currentUserId.set(user.id);
                                this.currentUserDni.set(user.dni);

                                this.showQuiz.set(true);

                                return EMPTY;

                            })

                        )

                )

            )
            .subscribe({

                error: () => {

                    this.currentUserId.set(user.id);
                    this.currentUserDni.set(user.dni);

                    this.showQuiz.set(true);

                }

            });

    }

}