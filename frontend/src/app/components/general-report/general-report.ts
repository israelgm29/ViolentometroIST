import {Component, computed, inject, OnInit, signal} from '@angular/core';
import {StatisticsService} from "../../services/statistics.service";
import {DataSharedService} from "../../services/data-shared.service";
import {DashboardData, ReportDetailDTO, VulnerabilityReportDTO} from "../../models/statistics";
import * as XLSX from 'xlsx';
import {DecimalPipe, NgClass} from "@angular/common";
import {RouterLink} from "@angular/router";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {
    MatDatepickerToggle,
    MatDateRangeInput,
    MatDateRangePicker,
    MatEndDate,
    MatStartDate
} from "@angular/material/datepicker";
import {FormsModule} from "@angular/forms";
import {MatFormField, MatLabel} from "@angular/material/input";
import {MatButton, MatIconButton} from "@angular/material/button";

@Component({
    selector: 'app-general-report',
    standalone: true,
    imports: [
        DecimalPipe,
        RouterLink,
        MatIcon,
        MatTooltip,
        NgClass,
        MatProgressSpinner,
        MatEndDate,
        FormsModule,
        MatStartDate,
        MatDatepickerToggle,
        MatDateRangePicker,
        MatDateRangeInput,
        MatLabel,
        MatFormField,
        MatButton,
        MatIconButton
    ],
    templateUrl: './general-report.html',
    styleUrl: './general-report.css',
})
export class GeneralReport implements OnInit {
    private statsService = inject(StatisticsService);
    public dataShared = inject(DataSharedService);

    public isLoading = signal(false);
    public exportingPdf = signal(false);
    public exportingExcel = signal(false);


    public vulnerabilityData = signal<VulnerabilityReportDTO[]>([]);
    public genderReport = signal<ReportDetailDTO[]>([]);
    public ethnicReport = signal<ReportDetailDTO[]>([]);
    public disabilityReport = signal<ReportDetailDTO[]>([]);


    public totalVictims = signal(0);
    public criticalRiskCount = signal(0);
    public alertLevel = signal('ESTABLE');


    public startDate = this.dataShared.startDate;
    public endDate = this.dataShared.endDate;


    public totalHighRisk = computed(() =>
        this.vulnerabilityData().reduce((a, v) => a + v.highRiskCount, 0)
    );
    public totalModerateRisk = computed(() =>
        this.vulnerabilityData().reduce((a, v) => a + v.moderateRiskCount, 0)
    );
    public totalParticipants = computed(() =>
        this.vulnerabilityData().reduce((a, v) => a + v.totalVictims, 0)
    );

    ngOnInit(): void {
        this.loadAll();
    }


    onDateChange(): void {
        this.loadAll();
    }

    loadAll(): void {
        this.isLoading.set(true);
        const start = this.dataShared.startISO();
        const end = this.dataShared.endISO();
        const surveyId = this.dataShared.selectedSurvey()?.id;

        // Carga dashboard general (vulnerabilidad + KPIs)
        this.statsService.getDashboardData(start, end, surveyId).subscribe({
            next: (data: DashboardData) => {
                this.totalVictims.set(data.totalVictims);
                this.criticalRiskCount.set(data.criticalRiskCount);
                this.alertLevel.set(data.alertLevel);
                this.vulnerabilityData.set(data.vulnerabilityTable || []);
            },
            error: err => {
                console.error(err);
                this.isLoading.set(false);
            }
        });

        // Cargas en paralelo de reportes detallados
        const types = ['genero', 'etnia', 'discapacidad'];
        let completed = 0;
        const done = () => {
            if (++completed === types.length) this.isLoading.set(false);
        };

        this.statsService.getDetailedReport('genero', start, end, surveyId).subscribe({
            next: d => {
                this.genderReport.set(d);
                done();
            },
            error: () => done()
        });
        this.statsService.getDetailedReport('etnia', start, end, surveyId).subscribe({
            next: d => {
                this.ethnicReport.set(d);
                done();
            },
            error: () => done()
        });
        this.statsService.getDetailedReport('discapacidad', start, end, surveyId).subscribe({
            next: d => {
                this.disabilityReport.set(d);
                done();
            },
            error: () => done()
        });
    }

    setToday(): void {
        const s = new Date();
        s.setHours(0, 0, 0, 0);
        const e = new Date();
        e.setHours(23, 59, 59, 999);
        this.dataShared.updateDates(s, e);
        this.loadAll();
    }

    setLastMonth(): void {
        const e = new Date();
        const s = new Date();
        s.setMonth(s.getMonth() - 1);
        this.dataShared.updateDates(s, e);
        this.loadAll();
    }


    getAlertClass(): string {
        const level = this.alertLevel();
        if (level === 'CRÍTICO') return 'alert-critico';
        if (level === 'ALERTA') return 'alert-alerta';
        return 'alert-estable';
    }

    getRiskBarWidth(high: number, total: number): string {
        if (!total) return '0%';
        return `${Math.round((high / total) * 100)}%`;
    }

    getPercentageBar(value: number, total: number): string {
        if (!total) return '0%';
        return `${Math.round((value / total) * 100)}%`;
    }

    sumValues(data: ReportDetailDTO[]): number {
        return data.reduce((a, d) => a + d.value, 0);
    }


    exportExcel(): void {
        this.exportingExcel.set(true);

        const wb = XLSX.utils.book_new();
        const survey = this.dataShared.selectedSurvey()?.title || 'General';
        const fecha = new Date().toLocaleDateString('es-EC');

        // Hoja 1: Vulnerabilidad
        const vulnData = [
            ['REPORTE GENERAL DE VULNERABILIDAD'],
            [`Cuestionario: ${survey}  |  Generado: ${fecha}`],
            [],
            ['Institución', 'Riesgo Alto', 'Riesgo Moderado', 'Total Participantes'],
            ...this.vulnerabilityData().map(v => [
                v.institutionName, v.highRiskCount, v.moderateRiskCount, v.totalVictims
            ]),
            [],
            ['TOTALES', this.totalHighRisk(), this.totalModerateRisk(), this.totalParticipants()]
        ];
        const ws1 = XLSX.utils.aoa_to_sheet(vulnData);
        ws1['!cols'] = [{wch: 35}, {wch: 14}, {wch: 17}, {wch: 20}];
        XLSX.utils.book_append_sheet(wb, ws1, 'Vulnerabilidad');

        // Hoja 2: Género
        const genderData = [
            ['DETALLE POR GÉNERO'],
            [`Cuestionario: ${survey}  |  Generado: ${fecha}`],
            [],
            ['Categoría', 'Grupo', 'Casos', 'Porcentaje'],
            ...this.genderReport().map(r => [r.label, r.group, r.value, `${r.percentage?.toFixed(1)}%`])
        ];
        const ws2 = XLSX.utils.aoa_to_sheet(genderData);
        ws2['!cols'] = [{wch: 30}, {wch: 20}, {wch: 10}, {wch: 12}];
        XLSX.utils.book_append_sheet(wb, ws2, 'Género');

        // Hoja 3: Etnia
        const ethnicData = [
            ['DETALLE POR ETNIA'],
            [`Cuestionario: ${survey}  |  Generado: ${fecha}`],
            [],
            ['Etnia', 'Grupo', 'Casos', 'Porcentaje'],
            ...this.ethnicReport().map(r => [r.label, r.group, r.value, `${r.percentage?.toFixed(1)}%`])
        ];
        const ws3 = XLSX.utils.aoa_to_sheet(ethnicData);
        ws3['!cols'] = [{wch: 25}, {wch: 20}, {wch: 10}, {wch: 12}];
        XLSX.utils.book_append_sheet(wb, ws3, 'Etnia');

        // Hoja 4: Discapacidad
        const discData = [
            ['DETALLE POR DISCAPACIDAD'],
            [`Cuestionario: ${survey}  |  Generado: ${fecha}`],
            [],
            ['Tipo', 'Grupo', 'Casos', 'Porcentaje'],
            ...this.disabilityReport().map(r => [r.label, r.group, r.value, `${r.percentage?.toFixed(1)}%`])
        ];
        const ws4 = XLSX.utils.aoa_to_sheet(discData);
        ws4['!cols'] = [{wch: 30}, {wch: 20}, {wch: 10}, {wch: 12}];
        XLSX.utils.book_append_sheet(wb, ws4, 'Discapacidad');

        XLSX.writeFile(wb, `reporte-general-${fecha}.xlsx`);
        this.exportingExcel.set(false);
    }


    exportPdf(): void {
        window.print();
    }
}
