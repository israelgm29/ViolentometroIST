import { Component, OnInit, signal, computed, inject, Input } from '@angular/core';
import { DecimalPipe, NgClass } from '@angular/common';
import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { QuestionZone } from "../../../models/question-zone";
import { ModalService } from "../../../services/modal.service";
import { UserAnswerService } from '../../../services/user-answer.service';
import { QuizResultService } from '../../../services/quiz-result.service';
import { ToastrService } from 'ngx-toastr';
import {QuizResult, QuizResultRequest, RiskLevel} from "../../../models/quiz-result";
import { QuestionDisplay } from "../../../components/question-display/question-display.component";
import { from, map, switchMap } from "rxjs";
import { EncryptionService } from "../../../services/encryption.service";
import { SurveyService } from "../../../services/survey.service";
import { ZoneService } from "../../../services/zone.service";
import { ViolenceZoneInterface } from "../../../models/zone";

@Component({
    selector: 'app-questions-container',
    standalone: true,
    imports: [MatIconModule, MatButtonModule, QuestionDisplay, NgClass, DecimalPipe],
    templateUrl: './questions-container.component.html',
    styleUrls: ['./questions-container.component.scss']
})
export class QuestionsContainerComponent implements OnInit {

    @Input() userId!: number;
    @Input() userDni!: string;
    @Input() surveyId!: number;  // ✅ recibido desde HomeComponent — evita doble llamada

    // Servicios
    private modalService = inject(ModalService);
    private userAnswerService = inject(UserAnswerService);
    private toastr = inject(ToastrService);
    private router = inject(Router);
    private quizResultService = inject(QuizResultService);
    private encryptionService = inject(EncryptionService);
    private surveyService = inject(SurveyService);
    private zoneService  = inject(ZoneService);

    // Todas las zonas disponibles — para el caso "todo NO"
    allZones = signal<ViolenceZoneInterface[]>([]);

    // Estado básico
    questions = signal<QuestionZone[]>([]);
    answers = signal<Map<number, boolean>>(new Map());
    currentIndex = signal(0);
    slideDirection = signal<'left' | 'right' | 'none'>('none');
    isSaving = signal(false);

    // Pregunta actual
    currentQuestion = computed(() => this.questions()[this.currentIndex()] || null);

    // Progreso (0-100)
    progress = computed(() => {
        const total = this.questions().length;
        return total > 0 ? (this.answers().size / total) * 100 : 0;
    });

    // Navegación
    canGoNext = computed(() => this.currentIndex() < this.questions().length - 1);
    isLastQuestion = computed(() => this.currentIndex() === this.questions().length - 1);

    riskLevel = computed((): RiskLevel => {
        let maxSeverity = 0;

        this.answers().forEach((isYes, questionId) => {
            if (isYes) {
                const pregunta = this.questions().find(q => q.id === questionId);
                if (pregunta?.zone && pregunta.zone.severity > maxSeverity) {
                    maxSeverity = pregunta.zone.severity;
                }
            }
        });

        return this.mapSeverityToRiskLevel(maxSeverity);
    });

    riskIcon = computed(() => {
        const icons: Record<RiskLevel, string> = {
            neutral: 'sentiment_satisfied',
            low: 'sentiment_neutral',
            medium: 'sentiment_dissatisfied',
            high: 'sentiment_very_dissatisfied',
            critical: 'dangerous'
        };
        return icons[this.riskLevel()];
    });

    ngOnInit() {
        if (!this.userId) {
            this.toastr.error('Error de sesión. Por favor, vuelva a iniciar.', 'Error');
            this.router.navigate(['/']);
            return;
        }
        this.loadZones();
        this.loadQuestionsAndResume();
    }

    private loadZones(): void {
        this.zoneService.getAllZones().subscribe({
            next: (zones) => this.allZones.set(zones),
            error: () => console.warn('No se pudieron cargar las zonas')
        });
    }

    private loadQuestionsAndResume() {
        from(this.encryptionService.encrypt(this.userDni)).pipe(
            switchMap(enc =>
                this.surveyService.getActiveSurvey().pipe(
                    switchMap(survey =>
                        // ✅ getByDniAndSurveyToday — solo respuestas de HOY
                        this.userAnswerService.getByDniAndSurveyToday(enc, survey.id).pipe(
                            map(previous => ({ survey, previous }))
                        )
                    )
                )
            )
        ).subscribe({
            next: ({ survey, previous }) => {
                if (!survey?.questions?.length) {
                    this.toastr.warning('No hay un cuestionario activo disponible.', 'Sin cuestionario');
                    setTimeout(() => this.router.navigate(['/']), 1500);
                    return;
                }

                const questions: QuestionZone[] = survey.questions.map(q => ({
                    id: q.id,
                    question: q.question,
                    questionNumber: q.questionNumber,
                    status: q.status,
                    zone: q.zone
                }));

                this.questions.set(questions);

                if (previous.length > 0) {
                    const mapResp = new Map<number, boolean>();
                    previous.forEach(a => mapResp.set(a.idQuestion, a.answer));
                    this.answers.set(mapResp);

                    if (previous.length >= questions.length) {
                        this.finishQuestionnaire();
                        return;
                    }

                    const nextIndex = questions.findIndex(q => !mapResp.has(q.id));
                    if (nextIndex !== -1) this.currentIndex.set(nextIndex);
                }
            },
            error: () => this.toastr.error('Error al cargar el cuestionario', 'Error')
        });
    }

    answerQuestion(answer: boolean) {
        const question = this.currentQuestion();
        if (!question || this.isSaving()) return;

        this.isSaving.set(true);
        this.userAnswerService.saveAnswer({
            idAppUser: this.userId,
            idQuestion: question.id,
            answer: answer
        }).subscribe({
            next: () => {
                this.answers.update(map => new Map(map).set(question.id, answer));
                this.isSaving.set(false);

                if (this.isLastQuestion()) {
                    this.finishQuestionnaire();
                } else {
                    setTimeout(() => this.nextQuestion(), 300);
                }
            },
            error: () => {
                this.isSaving.set(false);
                this.toastr.error('Error al guardar la respuesta.', 'Error');
            }
        });
    }

    /**
     * CÁLCULO FINAL DEL RESULTADO
     * Determina qué zona (mensajes del admin) se enviará al modal
     */
    private calculateResult(): QuizResult {
        let yesCount = 0;
        let dominantZone: any = null;

        this.answers().forEach((isYes, questionId) => {
            if (isYes) {
                yesCount++;
                const question = this.questions().find(q => q.id === questionId);
                if (question?.zone) {
                    if (!dominantZone || question.zone.severity > dominantZone.severity) {
                        dominantZone = question.zone;
                    }
                }
            }
        });

        // Si nadie respondio SI, usar la zona de menor severidad de TODAS las zonas
        if (!dominantZone) {
            const all = this.allZones();
            if (all.length > 0) {
                dominantZone = all.reduce((min: any, z: any) =>
                    z.severity < min.severity ? z : min
                );
            }
        }

        return {
            userId: this.userId,
            totalQuestions: this.questions().length,
            answeredQuestions: this.answers().size,
            yesCount,
            noCount: this.questions().length - yesCount,
            totalPoints: 0,
            riskLevel: this.mapSeverityToRiskLevel(dominantZone?.severity || 0),
            timestamp: new Date(),
            zone: dominantZone
        };
    }

    private mapSeverityToRiskLevel(severity: number): RiskLevel {
        if (severity <= 0) return 'neutral';
        if (severity === 1) return 'low';
        if (severity === 2) return 'medium';
        if (severity === 3) return 'high';
        return 'critical';
    }


    private finishQuestionnaire() {
        const localResult = this.calculateResult();

        // ✅ Creamos el objeto usando la interfaz QuizResultRequest
        const dataToSave: QuizResultRequest = {
            idAppUser: this.userId,
            idSurvey: this.surveyId,
            totalScore: localResult.yesCount,
            riskLevel: localResult.riskLevel,
            dominantZoneId: localResult.zone?.id
        };

        // 1. Guardamos localmente para que el modal funcione
        this.quizResultService.saveResult(localResult);

        // 2. Enviamos al backend para activar el bloqueo de "una vez al día"
        this.quizResultService.saveToBackend(dataToSave).subscribe({
            next: () => {
                setTimeout(() => {
                    this.router.navigate(['/welfare']);
                    this.modalService.openResultsModal();
                }, 800);
            },
            error: (err) => {
                console.error('Error al persistir el resultado final:', err);
                // Navegamos de todos modos para no arruinar la experiencia del usuario
                this.router.navigate(['/welfare']);
                this.modalService.openResultsModal();
            }
        });
    }

    nextQuestion() {
        if (this.canGoNext()) {
            this.slideDirection.set('left');
            this.currentIndex.update(i => i + 1);
            setTimeout(() => this.slideDirection.set('none'), 300);
        }
    }

    getMercuryColor(position: number): string {
        const risk = this.riskLevel();
        const intensity = position < 50 ? 0 : 1;
        const colors: Record<RiskLevel, [string, string]> = {
            critical: ['#ff6b6b', '#ff3838'],
            high: ['#ff6b6b', '#ff3838'],
            medium: ['#ffa726', '#ff9800'],
            low: ['#66bb6a', '#4caf50'],
            neutral: ['#64b5f6', '#42a5f5']
        };
        return colors[risk][intensity];
    }
}