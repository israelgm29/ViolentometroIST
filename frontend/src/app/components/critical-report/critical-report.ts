import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

import { StatisticsService } from '../../services/statistics.service';
import { DataSharedService } from '../../services/data-shared.service';
import { ReportService } from '../../services/report.service';
import { CriticalCase, CriticalCasesReport } from '../../models/case-reports';
import { CriticalCountPipe } from '../../shared/pipes/critical-count-pipe';
import { AvgScorePipe } from '../../shared/pipes/avg-score-pipe';

@Component({
    selector: 'app-critical-report',
    standalone: true,
    imports: [
        CommonModule, FormsModule, DatePipe,
        MatButtonModule, MatIconModule, MatTableModule, MatSortModule,
        MatInputModule, MatFormFieldModule, MatTooltipModule,
        MatProgressSpinnerModule, MatExpansionModule,
        MatSnackBarModule,
        CriticalCountPipe, AvgScorePipe,
    ],
    templateUrl: './critical-report.html',
    styleUrl: './critical-report.css'
})
export class CriticalReportComponent implements OnInit {

    private statisticsService = inject(StatisticsService);
    public  dataSharedService = inject(DataSharedService);
    private reportService     = inject(ReportService);
    private snackBar          = inject(MatSnackBar);
    private sanitizer         = inject(DomSanitizer);

    // ── Estado ──────────────────────────────────────────────────────
    public isLoading     = signal(false);
    public isExporting   = signal(false);
    public isPreviewOpen = signal(false);

    public report        = signal<CriticalCasesReport | null>(null);
    public safePdfUrl    = signal<SafeResourceUrl | null>(null);
    public pdfPreviewUrl = signal<string | null>(null);

    // ── Tabla ────────────────────────────────────────────────────────
    public displayedColumns = ['victimName', 'registeredAt', 'riskScore', 'alertCount', 'actions'];

    // ── Ciclo de vida ────────────────────────────────────────────────
    ngOnInit(): void { this.loadReport(); }

    // El dashboard llama a este método cuando cambian las fechas
    reload(): void { this.loadReport(); }

    // ── Carga ────────────────────────────────────────────────────────
    loadReport(): void {
        const surveyId = this.dataSharedService.selectedSurvey()?.id;
        if (!surveyId) return;

        this.isLoading.set(true);
        this.statisticsService.getCriticalCasesReport(
            this.dataSharedService.startISO(),
            this.dataSharedService.endISO(),
            surveyId
        ).subscribe({
            next:  d  => { this.report.set(d); this.isLoading.set(false); },
            error: () => this.isLoading.set(false)
        });
    }

    get filteredCases(): CriticalCase[] {
        return this.report()?.cases ?? [];
    }

    // ── PDF ──────────────────────────────────────────────────────────
    private generatePdf(callback: (url: string) => void): void {
        const surveyId = this.dataSharedService.selectedSurvey()?.id;
        if (!surveyId || !this.report()?.cases?.length) {
            this.snackBar.open('No hay datos disponibles', 'OK', { duration: 3000 });
            return;
        }

        this.isExporting.set(true);
        this.reportService.downloadCriticalCasesPdf({
            startDate: this.dataSharedService.startISO(),
            endDate:   this.dataSharedService.endISO(),
            surveyId
        }).subscribe({
            next: blob => {
                const url = window.URL.createObjectURL(blob);
                this.pdfPreviewUrl.set(url);
                this.safePdfUrl.set(this.sanitizer.bypassSecurityTrustResourceUrl(url));
                callback(url);
            },
            error:    () => this.snackBar.open('Error al generar el reporte', 'OK', { duration: 3000 }),
            complete: () => this.isExporting.set(false)
        });
    }

    previewPDF(): void {
        this.generatePdf(() => this.isPreviewOpen.set(true));
    }

    exportPDF(): void {
        this.generatePdf(url => {
            const a = document.createElement('a');
            a.href = url;
            a.download = `casos-criticos-${new Date().toISOString().slice(0, 10)}.pdf`;
            a.click();
        });
    }

    downloadPDF(): void {
        const url = this.pdfPreviewUrl();
        if (!url) return;
        const a = document.createElement('a');
        a.href = url;
        a.download = `casos-criticos-${new Date().toISOString().slice(0, 10)}.pdf`;
        a.click();
    }

    closePreview(): void {
        const url = this.pdfPreviewUrl();
        if (url) URL.revokeObjectURL(url);
        this.pdfPreviewUrl.set(null);
        this.isPreviewOpen.set(false);
    }

    // ── Helpers UI ───────────────────────────────────────────────────
    getRiskColor(level: string): string {
        return ({ 'CRÍTICO': '#e53935', 'ALTO': '#fb8c00', 'MODERADO': '#1e88e5' } as any)[level] ?? '#9e9e9e';
    }

    getRiskBadgeClass(level: string): string {
        return ({ 'CRÍTICO': 'badge-critical', 'ALTO': 'badge-high', 'MODERADO': 'badge-moderate' } as any)[level] ?? 'badge-moderate';
    }

    getRiskIcon(level: string): string {
        return ({ 'CRÍTICO': 'crisis_alert', 'ALTO': 'warning', 'MODERADO': 'info' } as any)[level] ?? 'info';
    }

    getGenderIcon(gender?: string): string {
        if (!gender) return 'person';
        const g = gender.toLowerCase();
        if (g.includes('fem'))  return 'face_3';
        if (g.includes('masc')) return 'face';
        return 'person';
    }

    getGenderClass(gender?: string): string {
        if (!gender) return 'other';
        const g = gender.toLowerCase();
        if (g.includes('fem'))  return 'female';
        if (g.includes('masc')) return 'male';
        return 'other';
    }
}