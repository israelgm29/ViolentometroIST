import {Component, ViewChild} from '@angular/core';
import {MatTableDataSource, MatTableModule} from "@angular/material/table";
import {MatPaginator, MatPaginatorModule} from "@angular/material/paginator";
import {QuestionsService} from "../../../services/questions.service";
import {MatIconModule} from "@angular/material/icon";
import {QuestionZone} from "../../../models/question-zone";
import {MatMenuModule} from "@angular/material/menu";
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
import {MatSort} from "@angular/material/sort";

@Component({
    selector: 'app-questions',
    standalone: true,
    imports: [
        CommonModule,
        MatTableModule,
        MatIconModule,
        MatPaginatorModule,
        MatMenuModule,
        MatButtonModule,
        MatTooltipModule,
        MatCardModule,
        MatProgressBarModule,
        MatSort
    ],
    templateUrl: './questions.html',
    styleUrl: './questions.scss',
})
export class QuestionsComponent {
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    filterValues = {
        search: '',
        zone: 'all'
    };
    displayedColumns: string[] = ['number', 'question', 'zone', 'status', 'actions'];
    dataSource = new MatTableDataSource<QuestionZone>();
    isExporting: boolean = false;


    constructor(public dialog: MatDialog, private questionService: QuestionsService, private toastr: ToastrService) {
    }

    ngOnInit(): void {
        this.loadQuestions();


        this.dataSource.filterPredicate = (data: QuestionZone, filter: string) => {
            const searchTerms = JSON.parse(filter);


            const searchStr = (data.question + ' ' + data.zone.name).toLowerCase();
            const matchesSearch = searchStr.includes(searchTerms.search.toLowerCase());


            let matchesZone = true;
            if (searchTerms.zone !== 'all') {
                matchesZone = data.zone.name.toLowerCase() === searchTerms.zone.toLowerCase();
            }

            return matchesSearch && matchesZone;
        };
    }

    loadQuestions() {
        // Usamos el endpoint que nos da QuestionZoneDTO con toda la info de la zona
        this.questionService.getQuestionsWithZone().subscribe(data => {
            this.dataSource.data = data;
            this.dataSource.paginator = this.paginator;
        });
    }

    // Función central que emite el filtro
    applyCombinedFilter() {
        this.dataSource.filter = JSON.stringify(this.filterValues);
        if (this.dataSource.paginator) {
            this.dataSource.paginator.firstPage();
        }
    }

    applyFilter(event: Event) {
        this.filterValues.search = (event.target as HTMLInputElement).value;
        this.applyCombinedFilter();
    }

    filterByZone(zone: 'all' | 'alerta amarilla' | 'alerta naranja' | 'alerta roja'): void {
        this.filterValues.zone = zone;
        this.applyCombinedFilter();
    }

    exportToExcel(): void {
        if (this.dataSource.data.length === 0) {
            this.toastr.warning('No hay preguntas para exportar');
            return;
        }

        this.isExporting = true;

        // Usamos el setTimeout para que la animación del icono se vea antes del proceso pesado
        setTimeout(() => {
            try {
                // 1. Mapeo de datos manejando la interfaz anidada QuestionZone
                const dataToExport = this.dataSource.data.map(q => ({
                    'N°': q.questionNumber || 'N/A', // Campo de Question
                    'Pregunta': q.question || 'Sin texto', // Campo de Question
                    'Zona': q.zone ? q.zone.name : 'Sin zona', // Anidado de ViolenceZoneInterface
                    'Nivel de Riesgo': q.zone ? q.zone.severity : 'N/A', // Anidado
                    'Color de Zona': q.zone ? q.zone.color : 'N/A', // Anidado
                    'Estado': q.status ? 'Activa' : 'Inactiva' // Campo de Question
                }));

                // 2. Crear Worksheet
                const worksheet = XLSX.utils.json_to_sheet(dataToExport);

                // 3. Configurar anchos de columna para legibilidad
                worksheet['!cols'] = [
                    { wch: 5 },  // N°
                    { wch: 60 }, // Pregunta (más ancha por el texto largo)
                    { wch: 20 }, // Zona
                    { wch: 15 }, // Nivel de Riesgo
                    { wch: 15 }, // Color
                    { wch: 10 }  // Estado
                ];

                // 4. Generar libro y descargar
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


    openDialog(question?: QuestionZone): void {
        const dialogRef = this.dialog.open(QuestionFormDialog, {
            width: '100%',
            maxWidth: '650px', // Un poco más angosto que institutos porque tiene menos campos
            data: question || null,
            disableClose: true
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                if (question) {
                    const payload = { ...result, id: question.id };
                    this.questionService.updateQuestion(payload.id, payload).subscribe({
                        next: () => {
                            this.toastr.success('Pregunta actualizada con éxito');
                            this.loadQuestions();
                        },
                        error: (error) => this.toastr.error('Error al actualizar')
                    });
                } else {
                    const { id, ...payload } = result;
                    this.questionService.createQuestion(payload).subscribe(() => {
                        this.toastr.success('Pregunta creada');
                        this.loadQuestions();
                    });
                }
            }
        });
    }

    getColorHex(colorName: string): string {
        const colors: { [key: string]: string } = {
            'verde': '#2e7d32',    // Verde Material
            'amarillo': '#fbc02d', // Amarillo Material
            'rojo': '#d32f2f',     // Rojo Material
            'naranja': '#ef6c00'
        };
        // Si el colorName existe en el mapa lo devuelve, si no, devuelve el string tal cual
        return colors[colorName.toLowerCase()] || colorName;
    }

    deleteQuestion(id: number) {
        const dialogRef = this.dialog.open(ConfirmDialog, {
            width: '350px',
            data: {message: '¿Estás seguro de que deseas eliminar esta Pregunta? Esta acción no se puede deshacer.'}
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.questionService.deleteQuestion(id).subscribe({
                    next: () => {
                        this.loadQuestions();
                        this.toastr.warning('La pregunta ha sido eliminada', 'Registro Borrado');
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
            data: question // Aquí pasamos el objeto completo que ya tiene la zona
        });
    }

// Contar preguntas por nombre de alerta/zona
    getQuestionCountByAlert(alertName: string): number {
        if (!this.dataSource.data) return 0;
        return this.dataSource.data.filter(q =>
            q.zone?.name.toLowerCase().includes(alertName.toLowerCase())
        ).length;
    }

/// Contar preguntas activas (públicas)
    getActiveQuestionsCount(): number {
        if (!this.dataSource.data) return 0;
        return this.dataSource.data.filter(q => q.status === true).length;
    }

    // Calcular porcentaje para las barras de progreso
    getAlertPercentage(alertName: string): number {
        const total = this.dataSource.data.length;
        if (total === 0) return 0;
        return (this.getQuestionCountByAlert(alertName) / total) * 100;
    }

    resetFilters() {
        this.filterValues = { search: '', zone: 'all' };
        this.applyCombinedFilter();
    }

    // Obtener un resumen de todas las alertas presentes en los datos
    getAlertSummary() {
        const summary: { name: string, count: number, color: string }[] = [];

        this.dataSource.data.forEach(q => {
            if (q.zone) {
                const existing = summary.find(s => s.name === q.zone.name);
                if (existing) {
                    existing.count++;
                } else {
                    summary.push({
                        name: q.zone.name,
                        count: 1,
                        color: q.zone.color
                    });
                }
            }
        });

        // Ordenar por severidad si es necesario o por nombre
        return summary.sort((a, b) => a.name.localeCompare(b.name));
    }

// Resumen de visibilidad
    getVisibilityStats() {
        const total = this.dataSource.data.length;
        const publicas = this.dataSource.data.filter(q => q.status).length;
        return {
            publicas,
            ocultas: total - publicas,
            porcentaje: total > 0 ? (publicas / total) * 100 : 0
        };
    }

    getMainAlert() {
        const summary = this.getAlertSummary();
        if (summary.length === 0) return { name: 'N/A', color: '#ccc' };
        return summary.reduce((prev, current) => (prev.count > current.count) ? prev : current);
    }

}
