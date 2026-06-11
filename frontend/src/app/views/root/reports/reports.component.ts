import {Component, inject, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatNativeDateModule} from '@angular/material/core';
import {MatSelectModule} from '@angular/material/select';
import {FormsModule} from '@angular/forms';

import {DataSharedService} from '../../../services/data-shared.service';
import {SurveyService} from '../../../services/survey.service';
import {ReportService} from '../../../services/report.service';
import {Survey} from '../../../models/survey';
import {Observable} from 'rxjs';
import {MatCard} from "@angular/material/card";

interface ReportCard {
    id: string;
    title: string;
    description: string;
    icon: string;
    category: string;
    available: boolean;
}

@Component({
    selector: 'app-reports',
    standalone: true,
    imports: [
        CommonModule, FormsModule,
        MatIconModule, MatButtonModule, MatTooltipModule,
        MatProgressSpinnerModule, MatFormFieldModule,
        MatDatepickerModule, MatNativeDateModule, MatSelectModule, MatCard
    ],
    templateUrl: './reports.component.html',
    styleUrl: './reports.component.scss'
})
export class ReportsComponent {
    public dataShared = inject(DataSharedService);
    private surveyService = inject(SurveyService);
    private reportService = inject(ReportService);

    // ── Estado ──────────────────────────────────────────────────────
    public surveys = signal<Survey[]>([]);
    public selectedSurvey = signal<Survey | null>(null);
    public startDate = signal<Date>(new Date(new Date().setMonth(new Date().getMonth() - 1)));
    public endDate = signal<Date>(new Date());
    public downloading = signal<string>(''); // id del reporte descargándose
    public trackingDni = signal<string>('');
    public startDate2 = signal<Date>(new Date(new Date().setMonth(new Date().getMonth() - 4)));
    public endDate2 = signal<Date>(new Date(new Date().setMonth(new Date().getMonth() - 1)));

    get startISO2(): string {
        return this.startDate2().toISOString();
    }

    get endISO2(): string {
        return this.endDate2().toISOString();
    }


    // ── Catálogo de reportes ─────────────────────────────────────────
    public readonly reports: ReportCard[] = [
        {
            id: 'general',
            title: 'Reporte General Institucional',
            description: 'Vulnerabilidad por institución, análisis demográfico por género, etnia y discapacidad. Incluye KPIs del período y tasa de criticidad.',
            icon: 'analytics',
            category: 'Institucional',
            available: true
        },
        {
            id: 'critical-pdf',
            title: 'Casos Críticos — PDF',
            description: 'Listado detallado de participantes con nivel de riesgo crítico o alto. Incluye señales de alerta detectadas por caso.',
            icon: 'crisis_alert',
            category: 'Institucional',
            available: true
        },
        {
            id: 'critical-excel',
            title: 'Casos Críticos — Excel',
            description: 'Mismo reporte de casos críticos en formato Excel con filtros automáticos, KPIs y formato condicional por nivel de riesgo.',
            icon: 'table_view',
            category: 'Institucional',
            available: true
        },
        {
            id: 'trends',
            title: 'Reporte de Tendencias',
            description: 'Evolución temporal de participaciones, niveles de riesgo y puntaje promedio por día en el período seleccionado.',
            icon: 'trending_up',
            category: 'Análisis',
            available: false
        },
        {
            id: 'geographic',
            title: 'Reporte Geográfico',
            description: 'Distribución de participantes por provincia de origen y zona geográfica con análisis de concentración de riesgo.',
            icon: 'map',
            category: 'Análisis',
            available: false
        },
        {
            id: 'student-tracking',
            title: 'Seguimiento por Estudiante',
            description: 'Historial completo de sesiones de un estudiante por DNI. Muestra evolución del nivel de riesgo, puntaje promedio y tendencia en el período seleccionado.',
            icon: 'person_search',
            category: 'Bienestar',
            available: true
        },
        {
            id: 'period-comparison',
            title: 'Comparativo de Períodos',
            description: 'Compara dos períodos personalizados: participación, casos críticos, puntaje promedio y tasa de criticidad. Incluye evaluación automática de mejora o deterioro.',
            icon: 'compare_arrows',
            category: 'Análisis',
            available: true
        },
        {
            id: 'participation',
            title: 'Participación por Fecha',
            description: 'Dos hojas: resumen diario con conteo por nivel de riesgo, y detalle individual de cada sesión con DNI, género, etnia, instituto y zona dominante.',
            icon: 'event_note',
            category: 'Operativo',
            available: true
        },

    ];

    public get categories(): string[] {
        return [...new Set(this.reports.map(r => r.category))];
    }

    public reportsByCategory(category: string): ReportCard[] {
        return this.reports.filter(r => r.category === category);
    }

    // ── Ciclo de vida ────────────────────────────────────────────────
    constructor() {
        this.loadSurveys();
    }

    loadSurveys(): void {
        this.surveyService.getAllSurveys().subscribe({
            next: data => {
                this.surveys.set(data);
                // Si ya hay uno seleccionado en el servicio compartido, usarlo
                const current = this.dataShared.selectedSurvey();
                if (current) this.selectedSurvey.set(current);
                else if (data.length > 0) this.selectedSurvey.set(data[0]);
            }
        });
    }

    // ── Helpers ──────────────────────────────────────────────────────
    get startISO(): string {
        return this.startDate().toISOString();
    }

    get endISO(): string {
        return this.endDate().toISOString();
    }

    setLastMonth(): void {
        const e = new Date();
        const s = new Date();
        s.setMonth(s.getMonth() - 1);
        this.startDate.set(s);
        this.endDate.set(e);
    }

    setToday(): void {
        const s = new Date();
        s.setHours(0, 0, 0, 0);
        const e = new Date();
        e.setHours(23, 59, 59, 999);
        this.startDate.set(s);
        this.endDate.set(e);
    }

    isDownloading(id: string): boolean {
        return this.downloading() === id;
    }

    canDownload(): boolean {
        return !!this.selectedSurvey();
    }

    // ── Descarga ─────────────────────────────────────────────────────
    download(report: ReportCard): void {
        if (!report.available || !this.canDownload()) return;

        const surveyId = this.selectedSurvey()!.id!;
        const body = {startDate: this.startISO, endDate: this.endISO, surveyId};
        const date = new Date().toISOString().slice(0, 10);

        this.downloading.set(report.id);

        let request$: Observable<Blob>;
        let filename: string;

        switch (report.id) {
            case 'general':
                request$ = this.reportService.downloadGeneralReportPdf(body);
                filename = `reporte-general-${date}.pdf`;
                break;
            case 'critical-pdf':
                request$ = this.reportService.downloadCriticalCasesPdf(body);
                filename = `casos-criticos-${date}.pdf`;
                break;
            case 'critical-excel':
                request$ = this.reportService.downloadCriticalCasesExcel(body);
                filename = `casos-criticos-${date}.xlsx`;
                break;
            case 'student-tracking':
                if (!this.trackingDni()) {
                    // El HTML mostrará el input — no descargar aún
                    return;
                }
                request$ = this.reportService.downloadStudentTrackingExcel({
                    dni: this.trackingDni(),
                    startDate: this.startISO,
                    endDate: this.endISO,
                    surveyId
                });
                filename = `seguimiento-${this.trackingDni()}-${date}.xlsx`;
                break;
            case 'period-comparison':
                request$ = this.reportService.downloadPeriodComparisonExcel({
                    startDate1: this.startISO,
                    endDate1: this.endISO,
                    startDate2: this.startISO2,
                    endDate2: this.endISO2,
                    surveyId
                });
                filename = `comparativo-periodos-${date}.xlsx`;
                break;

            case 'participation':
                request$ = this.reportService.downloadParticipationExcel(body);
                filename = `participacion-${date}.xlsx`;
                break;

            default:
                this.downloading.set('');
                return;
        }

        request$.subscribe({
            next: (blob) => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = filename;
                a.click();
                window.URL.revokeObjectURL(url);
                this.downloading.set('');
            },
            error: () => this.downloading.set('')
        });
    }


}