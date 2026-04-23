import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from "@angular/router";
import { StatisticsService } from "../../../services/statistics.service";
import { DataSharedService } from "../../../services/data-shared.service";
import { MatIcon } from "@angular/material/icon";
import { MatIconButton } from "@angular/material/button";
import { DecimalPipe, UpperCasePipe } from "@angular/common";
import { MatCard } from "@angular/material/card";
import { ChartComponent } from "ng-apexcharts";
import {MatProgressSpinner} from "@angular/material/progress-spinner";

@Component({
    selector: 'app-report-detail',
    standalone: true,
    imports: [
        MatIcon, MatIconButton, RouterLink,
        UpperCasePipe, MatCard, ChartComponent, DecimalPipe, MatProgressSpinner
    ],
    templateUrl: './report-detail.html',
    styleUrl: './report-detail.css',
})
export class ReportDetail implements OnInit {
    // Inyectamos servicios de forma moderna
    private route = inject(ActivatedRoute);
    private statsService = inject(StatisticsService);
    private dataSharedService = inject(DataSharedService);

    public type: string = '';
    public reportData = signal<any[]>([]); // Usamos signal para los datos
    public chartOptions = signal<any>(null); // Usamos signal para el gráfico
    public loading = signal<boolean>(true);

    ngOnInit(): void {
        // 1. Obtenemos el tipo de reporte de la URL
        this.route.params.subscribe(params => {
            this.type = params['type'];

            // 2. LEER FECHAS: Ya no hay .subscribe() para las fechas.
            // Simplemente leemos los signals computados del servicio.
            const start = this.dataSharedService.startISO();
            const end = this.dataSharedService.endISO();

            this.cargarDatos(start, end);
        });
    }

    cargarDatos(start: string, end: string) {
        this.loading.set(true);
        this.statsService.getDetailedReport(this.type, start, end).subscribe({
            next: (data) => {
                this.reportData.set(data);
                this.configurarGrafico(data);
                this.loading.set(false);
            },
            error: (err) => {
                console.error('Error al cargar detalle:', err);
                this.loading.set(false);
            }
        });
    }

    configurarGrafico(data: any[]) {
        if (this.type === 'preguntas') {
            this.chartOptions.set({
                series: [{ name: 'Incidencia', data: data.map(d => d.value) }],
                chart: { type: 'bar', height: 450 },
                plotOptions: { bar: { horizontal: true } },
                xaxis: { categories: data.map(d => d.label) },
                colors: ['#3f51b5']
            });
        } else {
            const categories = [...new Set(data.map(item => item.label))];
            const zones = [...new Set(data.map(item => item.group))];

            const series = zones.map(zoneName => {
                return {
                    name: zoneName,
                    data: categories.map(cat => {
                        const record = data.find(d => d.label === cat && d.group === zoneName);
                        return record ? record.value : 0;
                    }),
                    color: data.find(d => d.group === zoneName)?.color || '#ccc'
                };
            });

            this.chartOptions.set({
                series: series,
                chart: {
                    type: 'bar',
                    height: 400,
                    stacked: true,
                    toolbar: { show: true }
                },
                plotOptions: { bar: { horizontal: false, columnWidth: '55%' } },
                xaxis: { categories: categories },
                legend: { position: 'top' },
                fill: { opacity: 1 },
                tooltip: { y: { formatter: (val: number) => `${val} Estudiantes` } }
            });
        }
    }
}