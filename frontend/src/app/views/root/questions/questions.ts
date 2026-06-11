import {Component, ViewChild} from '@angular/core';
import {MatPaginator, MatPaginatorModule} from "@angular/material/paginator";
import {QuestionsService} from "../../../services/questions.service";
import {MatIconModule} from "@angular/material/icon";
import {QuestionZone} from "../../../models/question-zone";
import {CommonModule} from "@angular/common";
import {MatButtonModule} from "@angular/material/button";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatCardModule} from "@angular/material/card";
import * as XLSX from "xlsx";
import {ToastrService} from "ngx-toastr";
import {QuestionFormDialog} from "../../../components/question-form-dialog/question-form-dialog";
import {MatDialog} from "@angular/material/dialog";
import {MatProgressBarModule} from "@angular/material/progress-bar";
import {ConfirmDialog} from "../../../components/confirm-dialog/confirm-dialog";
import {QuestionDetailDialog} from "../../../components/question-detail-dialog/question-detail-dialog";
import {PageEvent} from '@angular/material/paginator';

@Component({
    selector: 'app-questions',
    standalone: true,
    imports: [
        CommonModule,
        MatIconModule,
        MatPaginatorModule,
        MatButtonModule,
        MatTooltipModule,
        MatCardModule,
        MatProgressBarModule
    ],
    templateUrl: './questions.html',
    styleUrl: './questions.scss',
})
export class QuestionsComponent {
    @ViewChild(MatPaginator) paginator!: MatPaginator;

    // Propiedades del nuevo estándar
    totalQuestions = 0;
    visibilityStats = {publicas: 0, porcentaje: 0};
    mainAlert = {name: '', count: 0, color: ''};
    alertSummary: { name: string; count: number; color: string }[] = [];

    // Variables de filtro (públicas para el template)
    currentFilter = 'all';  // 'all' | 'alerta amarilla' | 'alerta naranja' | 'alerta roja'
    visibilityFilter = 'all';  // 'all' | 'public' | 'hidden'

    // Variables internas
    private currentTab = 'all';   // 'all' | 'public' | 'hidden'
    private currentZone = 'all';  // 'all' | 'alerta amarilla' | ...
    private searchText = '';

    questions: QuestionZone[] = [];
    filteredQuestions: QuestionZone[] = [];
    isExporting = false;
    pagedQuestions: QuestionZone[] = [];

    pageSize = 12;
    currentPage = 0;

    constructor(
        public dialog: MatDialog,
        private questionService: QuestionsService,
        private toastr: ToastrService
    ) {
    }

    ngOnInit(): void {
        this.loadQuestions();
    }

    // ══════════════════════════════════════════════════════
    //  DATA LOADING & STATS
    // ══════════════════════════════════════════════════════
    loadQuestions() {
        this.questionService.getQuestionsWithZone().subscribe(data => {

            this.questions = data;
            this.filteredQuestions = [...data];

            this.updateStats();
            this.applyCombinedFilter();
            this.updatePagedData();
        });
    }

    private updateStats() {
        const data = this.questions;
        this.totalQuestions = data.length;
        const publicas = data.filter(q => q.status).length;
        this.visibilityStats = {
            publicas,
            porcentaje: this.totalQuestions ? Math.round((publicas / this.totalQuestions) * 100) : 0
        };
        this.alertSummary = this.buildAlertSummary();
        this.mainAlert = this.buildMainAlert();
    }

    private buildAlertSummary() {
        const summaryMap: { [name: string]: { name: string; count: number; color: string } } = {};
        this.questions.forEach(q => {
            if (q.zone) {
                if (!summaryMap[q.zone.name]) {
                    summaryMap[q.zone.name] = {name: q.zone.name, count: 0, color: q.zone.color};
                }
                summaryMap[q.zone.name].count++;
            }
        });
        return Object.values(summaryMap).sort((a, b) => a.name.localeCompare(b.name));
    }

    private buildMainAlert() {
        if (this.alertSummary.length === 0) {
            return {name: 'N/A', count: 0, color: '#ccc'};
        }
        return this.alertSummary.reduce((prev, curr) => (prev.count > curr.count ? prev : curr));
    }


    // ══════════════════════════════════════════════════════
    //  FILTER ACTIONS
    // ══════════════════════════════════════════════════════
    applyFilter(event: Event) {
        this.searchText = (event.target as HTMLInputElement)
            .value
            .trim()
            .toLowerCase();

        this.applyCombinedFilter();
    }

    filterByZone(zone: string) {
        this.currentZone = zone;
        this.currentFilter = zone;

        this.applyCombinedFilter();
    }

    setVisibilityFilter(filter: string) {
        this.currentTab = filter;
        this.visibilityFilter = filter;

        this.applyCombinedFilter();
    }

    private applyCombinedFilter() {

        this.filteredQuestions = this.questions.filter(question => {

            // BUSQUEDA
            const matchesText =
                !this.searchText ||
                question.question?.toLowerCase().includes(this.searchText) ||
                question.zone?.name?.toLowerCase().includes(this.searchText);

            // VISIBILIDAD
            const matchesVisibility =
                this.currentTab === 'all' ||
                (this.currentTab === 'public' && question.status) ||
                (this.currentTab === 'hidden' && !question.status);

            // ZONA
            const zoneName = question.zone?.name?.toLowerCase() || '';

            const matchesZone =
                this.currentZone === 'all' ||
                zoneName.includes(this.currentZone.toLowerCase()) ||
                this.currentZone.toLowerCase().includes(zoneName);

            return matchesText && matchesVisibility && matchesZone;
        });

        this.currentPage = 0;

        if (this.paginator) {
            this.paginator.firstPage();
        }

        this.updatePagedData();
    }

    onPageChange(event: PageEvent): void {

        this.pageSize = event.pageSize;
        this.currentPage = event.pageIndex;

        this.updatePagedData();
    }

    private updatePagedData(): void {

        const startIndex = this.currentPage * this.pageSize;
        const endIndex = startIndex + this.pageSize;

        this.pagedQuestions =
            this.filteredQuestions.slice(startIndex, endIndex);
    }

    resetFilters() {
        this.searchText = '';
        this.currentTab = 'all';
        this.currentZone = 'all';
        this.currentFilter = 'all';
        this.visibilityFilter = 'all';

        this.filteredQuestions = [...this.questions];

        this.currentPage = 0;

        this.updatePagedData();
    }

    // ══════════════════════════════════════════════════════
    //  CRUD & DIALOGS
    // ══════════════════════════════════════════════════════
    openDialog(question?: QuestionZone): void {
        const dialogRef = this.dialog.open(QuestionFormDialog, {
            width: '100%',
            maxWidth: '650px',
            data: question || null,
            disableClose: true
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                if (question) {
                    const payload = {...result, id: question.id};
                    this.questionService.updateQuestion(payload.id, payload).subscribe({
                        next: () => {
                            this.toastr.success('Pregunta actualizada con éxito');
                            this.loadQuestions();
                        },
                        error: () => this.toastr.error('Error al actualizar')
                    });
                } else {
                    const {id, ...payload} = result;
                    this.questionService.createQuestion(payload).subscribe({
                        next: () => {
                            this.toastr.success('Pregunta creada');
                            this.loadQuestions();
                        },
                        error: () => this.toastr.error('Error al crear la pregunta')
                    });
                }
            }
        });
    }

    deleteQuestion(id: number) {
        const dialogRef = this.dialog.open(ConfirmDialog, {
            width: '350px',
            data: {
                title: 'Eliminar pregunta',
                message: '¿Estás seguro de que deseas eliminar esta pregunta? Esta acción no se puede deshacer.'
            }
        });
        dialogRef.afterClosed().subscribe(confirmed => {
            if (confirmed) {
                this.questionService.deleteQuestion(id).subscribe({
                    next: () => {
                        this.toastr.warning('La pregunta ha sido eliminada', 'Registro Borrado');
                        this.loadQuestions();
                    },
                    error: () => this.toastr.error('Hubo un problema al intentar eliminar', 'Error')
                });
            }
        });
    }

    viewDetail(question: QuestionZone): void {
        this.dialog.open(QuestionDetailDialog, {
            width: '100%',
            maxWidth: '500px',
            data: question
        });
    }

    // ══════════════════════════════════════════════════════
    //  EXPORT
    // ══════════════════════════════════════════════════════
    exportToExcel(): void {
        if (this.filteredQuestions.length === 0) {
            this.toastr.warning('No hay preguntas para exportar');
            return;
        }

        this.isExporting = true;
        setTimeout(() => {
            try {
                const dataToExport = this.filteredQuestions.map(q => ({
                    'N°': q.questionNumber || 'N/A',
                    'Pregunta': q.question || 'Sin texto',
                    'Zona': q.zone?.name || 'Sin zona',
                    'Nivel de Riesgo': q.zone?.severity ?? 'N/A',
                    'Color de Zona': q.zone?.color || 'N/A',
                    'Estado': q.status ? 'Pública' : 'Oculta'
                }));

                const worksheet = XLSX.utils.json_to_sheet(dataToExport);
                worksheet['!cols'] = [
                    {wch: 5},
                    {wch: 60},
                    {wch: 20},
                    {wch: 15},
                    {wch: 15},
                    {wch: 10}
                ];

                const workbook = XLSX.utils.book_new();
                XLSX.utils.book_append_sheet(workbook, worksheet, 'Preguntas_Violentometro');
                const fileName = `Reporte_Preguntas_${new Date().getTime()}.xlsx`;
                XLSX.writeFile(workbook, fileName);
                this.toastr.success('Reporte de preguntas generado');
            } catch (error) {
                console.error('Error exportando preguntas:', error);
                this.toastr.error('Error al generar el Excel');
            } finally {
                this.isExporting = false;
            }
        }, 250);
    }

    // ══════════════════════════════════════════════════════
    //  UTILS
    // ══════════════════════════════════════════════════════
    getColorHex(colorName: string): string {
        const colors: { [key: string]: string } = {
            'verde': '#2e7d32',
            'amarillo': '#fbc02d',
            'rojo': '#d32f2f',
            'naranja': '#ef6c00'
        };
        return colors[colorName?.toLowerCase()] || colorName;
    }
}