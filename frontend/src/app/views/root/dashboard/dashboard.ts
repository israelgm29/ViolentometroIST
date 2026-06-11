import {Component, OnInit, inject, signal, ChangeDetectionStrategy, ViewChild} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {RouterLink} from '@angular/router';
import {
    NgApexchartsModule,
    ApexAxisChartSeries,
    ApexChart,
    ApexXAxis,
    ApexStroke,
    ApexFill,
    ApexTooltip,
    ApexDataLabels,
    ApexPlotOptions,
    ApexLegend
} from 'ng-apexcharts';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatChipsModule} from '@angular/material/chips';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatNativeDateModule} from '@angular/material/core';
import {MatMenuModule} from '@angular/material/menu';
import {MatDividerModule} from '@angular/material/divider';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatTabsModule} from '@angular/material/tabs';

import {animate, style, transition, trigger} from '@angular/animations';

import {StatisticsService} from '../../../services/statistics.service';
import {DataSharedService} from '../../../services/data-shared.service';
import {SurveyService} from '../../../services/survey.service';
import {ReportService} from '../../../services/report.service';
import {Survey} from '../../../models/survey';
import {DashboardData, TrendResponseDTO} from '../../../models/statistics';
import {CriticalReportComponent} from '../../../components/critical-report/critical-report';

export type ChartOptions = {
    series: ApexAxisChartSeries | number[];
    chart: ApexChart;
    xaxis?: ApexXAxis;
    stroke?: ApexStroke;
    fill?: ApexFill;
    tooltip?: ApexTooltip;
    dataLabels?: ApexDataLabels;
    plotOptions?: ApexPlotOptions;
    colors?: string[];
    labels?: string[];
    legend?: ApexLegend;
};

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [
        CommonModule, FormsModule, RouterLink, NgApexchartsModule,
        MatCardModule, MatButtonModule, MatIconModule, MatChipsModule,
        MatFormFieldModule, MatInputModule, MatDatepickerModule, MatNativeDateModule,
        MatMenuModule, MatDividerModule, MatTooltipModule, MatProgressSpinnerModule,
        MatTabsModule, CriticalReportComponent
    ],
    templateUrl: './dashboard.html',
    styleUrl: './dashboard.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: [
        trigger('fadeIn', [
            transition(':enter', [
                style({opacity: 0, transform: 'translateY(10px)'}),
                animate('400ms ease-out', style({opacity: 1, transform: 'translateY(0)'}))
            ])
        ])
    ]
})
export class Dashboard implements OnInit {
    private statisticsService = inject(StatisticsService);
    public dataSharedService = inject(DataSharedService);
    private surveyService = inject(SurveyService);
    private reportService = inject(ReportService);

    // Referencia al componente hijo de casos críticos
    @ViewChild(CriticalReportComponent)
    private criticalReport?: CriticalReportComponent;

    // Signals públicos
    public view = signal<'select' | 'dashboard'>('select');
    public isLoading = signal(false);
    public isLoadingSurveys = signal(false);
    public activeTabIndex = signal(0);
    public surveys = signal<Survey[]>([]);
    public isDownloading = signal(false);
    public downloadingType = signal<string>('');

    // KPIs
    public totalVictimas = signal(0);
    public criticalRiskCount = signal(0);
    public alertLevel = signal('ESTABLE');
    public zonaMasFrecuente = signal('N/A');
    public totalRespuestas = signal(0);
    public etniasMasAfectadas = signal('N/A');

    // true cuando hay datos cargados para el período actual
    public hasData = signal(false);

    // Fechas
    public startDate = this.dataSharedService.startDate;
    public endDate = this.dataSharedService.endDate;

    // Gráficos
    public zoneChartOptions = signal<ChartOptions | null>(null);
    public trendChartOptions = signal<ChartOptions | null>(null);
    public genderChartOptions = signal<ChartOptions | null>(null);
    public disabilityChartOptions = signal<ChartOptions | null>(null);
    public etniaChartOptions = signal<ChartOptions | null>(null);
    public topQuestionsOptions = signal<ChartOptions | null>(null);
    public regionChartOptions = signal<ChartOptions | null>(null);
    public regionsData = signal<{ label: string; count: number }[]>([]);

    // Tendencias
    public trendsData = signal<TrendResponseDTO | null>(null);
    public participationChart = signal<ChartOptions | null>(null);
    public riskLevelChart = signal<ChartOptions | null>(null);
    public avgScoreChart = signal<ChartOptions | null>(null);

    ngOnInit(): void {
        this.loadSurveys();
    }

    loadSurveys(): void {
        this.isLoadingSurveys.set(true);
        this.surveyService.getAllSurveys().subscribe({
            next: data => {
                this.surveys.set(data);
                this.isLoadingSurveys.set(false);
            },
            error: () => this.isLoadingSurveys.set(false)
        });
    }

    selectSurvey(survey: Survey): void {
        this.dataSharedService.selectedSurvey.set(survey);
        this.view.set('dashboard');
        this.loadAllStatistics();
    }

    backToSelector(): void {
        this.view.set('select');
    }

    onDateChange(): void {
        this.loadAllStatistics();
        if (this.activeTabIndex() === 3) this.loadTrends();
        if (this.activeTabIndex() === 4) this.criticalReport?.loadReport();
    }

    onTabChange(index: number): void {
        this.activeTabIndex.set(index);
        if (index === 3) this.loadTrends();
        if (index === 4) this.criticalReport?.loadReport();
    }

    loadAllStatistics(): void {
        const surveyId = this.dataSharedService.selectedSurvey()?.id;
        if (!surveyId) return;

        this.isLoading.set(true);
        const start = this.dataSharedService.startISO();
        const end = this.dataSharedService.endISO();

        this.statisticsService.getDashboardData(start, end, surveyId).subscribe({
            next: (data: DashboardData) => {
                this.updateKPIs(data);
                this.updateCharts(data);
                this.isLoading.set(false);
            },
            error: () => this.isLoading.set(false)
        });
    }

    private updateKPIs(data: DashboardData): void {
        this.totalVictimas.set(data.totalVictims);
        this.criticalRiskCount.set(data.criticalRiskCount);
        this.alertLevel.set(data.alertLevel);
        this.hasData.set(data.totalVictims > 0);

        if (data.zones?.length) {
            const top = data.zones.reduce((a, b) => a.totalAnswers > b.totalAnswers ? a : b);
            this.zonaMasFrecuente.set(top.zoneName);
        }

        if (data.ethnics?.length) {
            const top = data.ethnics.reduce((a, b) => a.count > b.count ? a : b);
            this.etniasMasAfectadas.set(top.label);
        }

        if (data.alertsTrend?.length) {
            const total = data.alertsTrend.reduce((sum, t) => sum + t.count, 0);
            this.totalRespuestas.set(total);
        }
    }

    private updateCharts(data: DashboardData): void {
        if (data.zones) this.renderZoneChart(data.zones);
        if (data.alertsTrend) this.renderTrendChart(data.alertsTrend);
        if (data.genders) this.renderGenderChart(data.genders);
        if (data.disabilities) this.renderDisabilityChart(data.disabilities);
        if (data.ethnics) this.renderEtniaChart(data.ethnics);
        if (data.topQuestions) this.renderTopQuestions(data.topQuestions);
        if (data.regions) this.renderRegionChart(data.regions);
    }

    private renderZoneChart(zones: any[]): void {
        this.zoneChartOptions.set({
            series: zones.map(z => z.totalAnswers || z.count || 0),
            chart: {type: 'donut', height: 380, animations: {enabled: true, speed: 800}},
            labels: zones.map(z => z.zoneName || z.label),
            colors: zones.map(z => z.color || '#94a3b8'),
            plotOptions: {pie: {donut: {size: '65%'}}},
            legend: {position: 'right', fontSize: '12px'}
        });
    }

    private renderTrendChart(trendData: any[]): void {
        this.trendChartOptions.set({
            series: [{name: 'Alertas', data: trendData.map(t => t.count)}],
            chart: {type: 'area', height: 400, toolbar: {show: false}, animations: {enabled: true, speed: 800}},
            stroke: {curve: 'smooth', width: 3},
            xaxis: {categories: trendData.map(t => t.label)},
            colors: ['#6d28d9'],
            fill: {type: 'gradient', gradient: {shadeIntensity: 1, opacityFrom: 0.7, opacityTo: 0.2}}
        });
    }

    private renderGenderChart(data: any[]): void {
        this.genderChartOptions.set({
            series: data.map(d => d.count),
            chart: {type: 'pie', height: 300, animations: {enabled: true, speed: 800}},
            labels: data.map(d => d.label),
            colors: ['#0ea5e9', '#ec4899', '#94a3b8'],
            legend: {position: 'bottom'}
        });
    }

    private renderEtniaChart(data: any[]): void {
        this.etniaChartOptions.set({
            series: [{name: 'Casos', data: data.map(d => d.count)}],
            chart: {type: 'bar', height: 280, animations: {enabled: true, speed: 800}},
            xaxis: {categories: data.map(d => d.label)},
            plotOptions: {bar: {borderRadius: 4, distributed: true}},
            colors: ['#6d28d9', '#0d9488', '#d97706', '#8b5cf6', '#ec4899']
        });
    }

    private renderDisabilityChart(data: any[]): void {
        this.disabilityChartOptions.set({
            series: data.map(d => d.count),
            chart: {type: 'donut', height: 300, animations: {enabled: true, speed: 800}},
            labels: data.map(d => d.label),
            colors: ['#10b981', '#f59e0b'],
            plotOptions: {pie: {donut: {size: '65%'}}},
            legend: {position: 'bottom'}
        });
    }

    private renderTopQuestions(questions: any[]): void {
        this.topQuestionsOptions.set({
            series: [{name: 'Frecuencia', data: questions.map(q => q.count)}],
            chart: {type: 'bar', height: 280, animations: {enabled: true, speed: 800}},
            plotOptions: {bar: {horizontal: true, borderRadius: 4}},
            xaxis: {categories: questions.map(q => q.label.substring(0, 30) + '...')},
            colors: ['#ef4444']
        });
    }

    private renderRegionChart(data: any[]): void {
        // Ordenar de mayor a menor para mejor lectura
        const sorted = [...data].sort((a, b) => b.count - a.count);
        this.regionsData.set(sorted);

        const total = sorted.reduce((sum, r) => sum + r.count, 0);
        this.regionChartOptions.set({
            series: [{name: 'Estudiantes', data: sorted.map(r => r.count)}],
            chart: {
                type: 'bar',
                height: Math.max(300, sorted.length * 36),
                toolbar: {show: false},
                animations: {enabled: true, speed: 800}
            },
            plotOptions: {bar: {horizontal: true, borderRadius: 4, distributed: false}},
            xaxis: {categories: sorted.map(r => r.label)},
            colors: ['#6d28d9'],
            dataLabels: {
                enabled: true,
                formatter: (val: number) => {
                    const pct = total > 0 ? ((val / total) * 100).toFixed(1) : '0';
                    return `${val} (${pct}%)`;
                },
                style: {fontSize: '11px'}
            },
            tooltip: {
                y: {
                    formatter: (val: number) => {
                        const pct = total > 0 ? ((val / total) * 100).toFixed(1) : '0';
                        return `${val} estudiantes (${pct}%)`;
                    }
                }
            }
        });
    }

    getTotalRegions(): number {
        return this.regionsData().reduce((sum, r) => sum + r.count, 0);
    }

    getRegionPercentage(count: number): number {
        const total = this.getTotalRegions();
        return total > 0 ? Math.round((count / total) * 100) : 0;
    }

    // ─── TENDENCIAS ────────────────────────────────────────────────────────────

    loadTrends(): void {
        const surveyId = this.dataSharedService.selectedSurvey()?.id;
        if (!surveyId) return;

        this.statisticsService.getTrends(
            this.dataSharedService.startISO(),
            this.dataSharedService.endISO(),
            surveyId
        ).subscribe({
            next: (data: TrendResponseDTO) => {
                this.trendsData.set(data);
                this.renderParticipationChart(data);
                this.renderRiskLevelChart(data);
                this.renderAvgScoreChart(data);
            },
            error: err => console.error('Error cargando tendencias:', err)
        });
    }

    private renderParticipationChart(data: TrendResponseDTO): void {
        const dates = data.participationTrend.map(d => d.label);
        this.participationChart.set({
            series: [
                {name: 'Participaciones', data: data.participationTrend.map(d => d.count)},
                {name: 'Críticos', data: data.criticalTrend.map(d => d.count)}
            ],
            chart: {type: 'area', height: 300, toolbar: {show: false}, animations: {enabled: true, speed: 800}},
            stroke: {curve: 'smooth', width: 2},
            fill: {type: 'gradient', gradient: {shadeIntensity: 1, opacityFrom: 0.5, opacityTo: 0.1}},
            xaxis: {categories: dates},
            colors: ['#6d28d9', '#ef4444'],
            tooltip: {x: {format: 'dd MMM yyyy'}}
        });
    }

    private renderRiskLevelChart(data: TrendResponseDTO): void {
        if (!data.riskLevelSeries?.length) return;

        const colorMap: Record<string, string> = {
            critical: '#ef4444', high: '#f97316', medium: '#eab308', low: '#22c55e'
        };
        const labelMap: Record<string, string> = {
            critical: 'Crítico', high: 'Alto', medium: 'Medio', low: 'Bajo'
        };

        this.riskLevelChart.set({
            series: data.riskLevelSeries.map(s => ({
                name: labelMap[s.name] ?? s.name,
                data: s.data
            })),
            chart: {type: 'bar', height: 300, stacked: true, toolbar: {show: false}},
            xaxis: {categories: data.riskLevelSeries[0].dates},
            colors: data.riskLevelSeries.map(s => colorMap[s.name] ?? '#94a3b8'),
            plotOptions: {bar: {borderRadius: 2}},
            legend: {position: 'top'}
        });
    }

    private renderAvgScoreChart(data: TrendResponseDTO): void {
        this.avgScoreChart.set({
            series: [{name: 'Puntaje promedio', data: data.avgScoreTrend.map(d => d.count)}],
            chart: {type: 'line', height: 220, toolbar: {show: false}, animations: {enabled: true, speed: 800}},
            stroke: {curve: 'smooth', width: 3},
            xaxis: {categories: data.avgScoreTrend.map(d => d.label)},
            colors: ['#0ea5e9'],
            dataLabels: {enabled: true, style: {fontSize: '11px'}}
        });
    }

    getAlertColor(): string {
        return this.criticalRiskCount() > 0 ? '#ef4444' : '#10b981';
    }

    downloadReport(type: string): void {
        const surveyId = this.dataSharedService.selectedSurvey()?.id;
        if (!surveyId) return;

        this.isDownloading.set(true);
        this.downloadingType.set(type);

        const body = {
            startDate: this.dataSharedService.startISO(),
            endDate: this.dataSharedService.endISO(),
            surveyId
        };

        this.reportService.downloadGeneralReportPdf(body).subscribe({
            next: (blob) => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                const date = new Date().toISOString().slice(0, 10);
                a.href = url;
                a.download = `reporte-${type}-${date}.pdf`;
                a.click();
                window.URL.revokeObjectURL(url);
                this.isDownloading.set(false);
                this.downloadingType.set('');
            },
            error: () => {
                this.isDownloading.set(false);
                this.downloadingType.set('');
            }
        });
    }

    setToday(): void {
        const s = new Date();
        s.setHours(0, 0, 0, 0);
        const e = new Date();
        e.setHours(23, 59, 59, 999);
        this.dataSharedService.updateDates(s, e);
        this.loadAllStatistics();
        if (this.activeTabIndex() === 3) this.loadTrends();
        if (this.activeTabIndex() === 4) this.criticalReport?.loadReport();
    }

    setLastMonth(): void {
        const e = new Date();
        const s = new Date();
        s.setMonth(s.getMonth() - 1);
        this.dataSharedService.updateDates(s, e);
        this.loadAllStatistics();
        if (this.activeTabIndex() === 3) this.loadTrends();
        if (this.activeTabIndex() === 4) this.criticalReport?.loadReport();
    }

    downloadChart(chartId: string): void {
        console.log(`Descarga solicitada para: ${chartId}`);
    }
}