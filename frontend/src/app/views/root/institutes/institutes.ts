import {Component, OnInit, signal, ViewChild} from '@angular/core';
import {MatTableDataSource, MatTableModule} from "@angular/material/table";
import {MatDialog} from "@angular/material/dialog";
import {InstituteService} from "../../../services/institute.service";
import {InterfaceInstitute} from "../../../models/institute";
import {MatIcon} from "@angular/material/icon";
import {MatCard, MatCardContent} from "@angular/material/card";
import {MatButtonModule} from "@angular/material/button";
import {InstituteDialog} from "../../../components/institute-dialog/institute-dialog";
import {MatPaginator, MatPaginatorModule} from "@angular/material/paginator";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {ConfirmDialog} from "../../../components/confirm-dialog/confirm-dialog";
import {ToastrService} from "ngx-toastr";
import {InstituteDetailDialog} from "../../../components/intitute-detail/institute-detail-dialog";
import {MatTooltip} from "@angular/material/tooltip";
import * as XLSX from 'xlsx';
import {MatMenu, MatMenuModule, MatMenuTrigger} from "@angular/material/menu";

@Component({
    selector: 'app-institutes',
    imports: [
        MatTableModule,
        MatIcon,
        MatCard,
        MatCardContent,
        MatButtonModule,
        MatTooltip,
        MatPaginatorModule,
        MatFormFieldModule,
        MatInputModule,
        MatMenu, MatMenuModule,
        MatMenuTrigger
    ],
    templateUrl: './institutes.html',
    styleUrl: './institutes.scss',
})
export class Institutes implements OnInit {
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    displayedColumns: string[] = ['code', 'name', 'city', 'province', 'phone', 'email', 'status', 'actions'];
    institutes = signal<InterfaceInstitute[]>([]);
    public dataSource = new MatTableDataSource<any>([]);
    isExporting = false;


    constructor(public dialog: MatDialog, private instituteService: InstituteService, private toastr: ToastrService) {
    }

    ngAfterViewInit() {
        // Conectamos el paginador al dataSource
        this.dataSource.paginator = this.paginator;

        // Opcional: Cambiar las etiquetas a español
        this.paginator._intl.itemsPerPageLabel = 'Items por página:';
        this.paginator._intl.nextPageLabel = 'Siguiente';
        this.paginator._intl.previousPageLabel = 'Anterior';
    }

    ngOnInit(): void {
        this.loadInstitutes();
    }

    private loadInstitutes() {
        this.instituteService.getInstitutes().subscribe({
            next: (institutes) => {
                this.institutes.set(institutes);
                // 2. Actualiza el dataSource explícitamente
                this.dataSource.data = institutes;
            }
        });
    }

    openDialog(institute?: InterfaceInstitute): void {
        const dialogRef = this.dialog.open(InstituteDialog, {
            width: '95%',
            maxWidth: '900px',
            maxHeight: '90vh',
            data: institute,
            autoFocus: false,
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                if (institute?.id) {
                    this.updateInstitute(institute.id, result);
                } else {
                    this.createInstitute(result);
                }
            }
        });
    }

    createInstitute(data: any): void {
        const pendingLogo: File | null = data._pendingLogo || null;
        const {_pendingLogo, ...institute} = data;

        this.instituteService.saveInstitute(institute).subscribe({
            next: (saved) => {
                // Si había un logo pendiente, subirlo ahora que tenemos el ID
                if (pendingLogo && saved.id) {
                    this.instituteService.uploadLogo(saved.id, pendingLogo).subscribe({
                        next: () => {
                            this.loadInstitutes();
                            this.toastr.success('Instituto creado con logo', '¡Éxito!');
                        },
                        error: () => {
                            this.loadInstitutes();
                            this.toastr.warning('Instituto creado, pero no se pudo subir el logo');
                        }
                    });
                } else {
                    this.loadInstitutes();
                    this.toastr.success('Instituto creado correctamente', '¡Éxito!');
                }
            },
            error: () => this.toastr.error('No se pudo guardar el registro', 'Error')
        });
    }

    updateInstitute(id: number, data: any): void {
        const pendingLogo: File | null = data._pendingLogo || null;
        const {_pendingLogo, ...institute} = data;

        this.instituteService.updateInstitute(id, institute).subscribe({
            next: () => {
                if (pendingLogo) {
                    this.instituteService.uploadLogo(id, pendingLogo).subscribe({
                        next: () => {
                            this.loadInstitutes();
                            this.toastr.success('Actualizado con logo', '¡Éxito!');
                        },
                        error: () => {
                            this.loadInstitutes();
                            this.toastr.warning('Actualizado, pero no se pudo subir el logo');
                        }
                    });
                } else {
                    this.loadInstitutes();
                    this.toastr.success('Actualizado con éxito');
                }
            },
            error: (err) => console.error(err)
        });
    }


    deleteInstitute(id: number): void {
        const dialogRef = this.dialog.open(ConfirmDialog, {
            width: '350px',
            data: {message: '¿Estás seguro de que deseas eliminar este instituto? Esta acción no se puede deshacer.'}
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.instituteService.deleteInstitute(id).subscribe({
                    next: () => {
                        this.loadInstitutes();
                        this.toastr.warning('El instituto ha sido eliminado', 'Registro Borrado');
                    },
                    error: () => this.toastr.error('Hubo un problema al intentar eliminar', 'Error')
                });
            }
        });
    }

    applyFilter(event: Event) {
        const filterValue = (event.target as HTMLInputElement).value;
        this.dataSource.filter = filterValue.trim().toLowerCase();

        if (this.dataSource.paginator) {
            this.dataSource.paginator.firstPage();
        }
    }

    viewDetail(institute: InterfaceInstitute): void {
        this.dialog.open(InstituteDetailDialog, {
            width: '600px',
            data: institute,
            autoFocus: false
        });
    }

    // Calcula cuántos están activos
    getActiveCount(): number {
        return this.dataSource.data.filter(inst => inst.status === true).length;
    }

// Obtiene el nombre del último instituto agregado basándose en la fecha
    getLatestInstituteName(): string {
        if (this.dataSource.data.length === 0) return 'Ninguno';

        const sorted = [...this.dataSource.data].sort((a, b) => {
            return new Date(b.createdDate!).getTime() - new Date(a.createdDate!).getTime();
        });

        return sorted[0].shortName || sorted[0].name;
    }

    exportToExcel(): void {
        if (this.dataSource.data.length === 0) {
            this.toastr.warning('No hay datos para exportar');
            return;
        }

        // 1. Activamos la animación inmediatamente
        this.isExporting = true;

        // 2. Usamos setTimeout para permitir que Angular renderice el icono de carga antes de procesar
        setTimeout(() => {
            try {
                // 3. Mapeo de datos con nombres claros para las columnas de Excel
                const dataToExport = this.dataSource.data.map(inst => ({
                    'ID': inst.id,
                    'Código': inst.code,
                    'Nombre del Instituto': inst.name,
                    'Siglas': inst.shortName || 'N/A',
                    'Dirección': inst.address,
                    'Ciudad': inst.city,
                    'Provincia': inst.province,
                    'País': inst.country,
                    'Teléfono': inst.phone,
                    'Correo Electrónico': inst.email,
                    'Sitio Web': inst.web_url || 'No registrado',
                    'Estado': inst.status ? 'Activo' : 'Inactivo',
                    'Fecha de Registro': inst.createdDate ? new Date(inst.createdDate).toLocaleDateString() : 'N/A'
                }));

                // 4. Creación del libro de Excel
                const worksheet = XLSX.utils.json_to_sheet(dataToExport);

                // 5. Configuración de anchos de columna para que se vea profesional
                worksheet['!cols'] = [
                    {wch: 5}, {wch: 10}, {wch: 40}, {wch: 15}, {wch: 30},
                    {wch: 15}, {wch: 15}, {wch: 10}, {wch: 15}, {wch: 30},
                    {wch: 30}, {wch: 10}, {wch: 15}
                ];

                const workbook = XLSX.utils.book_new();
                XLSX.utils.book_append_sheet(workbook, worksheet, 'Institutos');

                // 6. Descarga del archivo con timestamp para evitar nombres duplicados
                const fileName = `Reporte_Institutos_${new Date().getDate()}.xlsx`;
                XLSX.writeFile(workbook, fileName);

                this.toastr.success('Reporte generado correctamente', '¡Éxito!');

            } catch (error) {
                console.error('Error al exportar Excel:', error);
                this.toastr.error('No se pudo generar el reporte', 'Error');
            } finally {
                // 7. Desactivamos la animación pase lo que pase
                this.isExporting = false;
            }
        }, 250); // Pequeña pausa de 250ms para que el usuario vea el icono girar
    }

    filterByStatus(status: 'all' | 'active' | 'inactive'): void {
        // 1. Si queremos ver todos, simplemente quitamos cualquier filtro previo
        if (status === 'all') {
            this.dataSource.filter = '';
            // Si tienes un input de búsqueda, es bueno limpiarlo visualmente
            // this.input.nativeElement.value = '';
        } else {
            // 2. Para filtrar por booleanos, convertimos el valor a string
            // porque el 'filter' de MatTableDataSource espera un string
            const filterValue = status === 'active' ? 'true' : 'false';

            // Configuramos el predicado de filtro para que busque específicamente en la columna status
            this.dataSource.filterPredicate = (data: InterfaceInstitute, filter: string) => {
                return data.status?.toString() === filter;
            };

            this.dataSource.filter = filterValue;
        }

        if (this.dataSource.paginator) {
            this.dataSource.paginator.firstPage();
        }
    }

}

