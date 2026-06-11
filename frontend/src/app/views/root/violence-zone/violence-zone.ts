import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatCard, MatCardContent } from "@angular/material/card";
import { MatIconModule } from "@angular/material/icon";
import { MatDialog } from "@angular/material/dialog";
import { ZoneService } from "../../../services/zone.service";
import { ToastrService } from "ngx-toastr";
import { ViolenceZoneInterface } from "../../../models/zone";
import { MatButtonModule } from "@angular/material/button";
import { MatTooltipModule } from "@angular/material/tooltip";
import {
    MatTableDataSource,
    MatTableModule
} from "@angular/material/table";
import { MatMenu, MatMenuModule, MatMenuTrigger } from "@angular/material/menu";
import * as XLSX from "xlsx";
import { MatPaginator, MatPaginatorModule } from "@angular/material/paginator";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { ZoneFormDialog } from "../../../components/zone-form-dialog/zone-form-dialog";
import { ZoneDetailDialog } from "../../../components/zone-detail-dialog/zone-detail-dialog";
import { ConfirmDialog } from "../../../components/confirm-dialog/confirm-dialog";
import { MatProgressBar } from "@angular/material/progress-bar";

@Component({
    selector: 'app-violence-zone',
    standalone: true,
    imports: [
        MatTableModule,
        MatIconModule,
        MatCard,
        MatCardContent,
        MatButtonModule,
        MatTooltipModule,
        MatPaginatorModule,
        MatFormFieldModule,
        MatInputModule,
        MatMenu, MatMenuModule,
        MatMenuTrigger, MatProgressBar
    ],
    templateUrl: './violence-zone.html',
    styleUrl: './violence-zone.scss',
})
export class ViolenceZone implements OnInit, AfterViewInit {
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild('input') searchInput!: ElementRef;
    zones: ViolenceZoneInterface[] = [];
    dataSource = new MatTableDataSource<ViolenceZoneInterface>([]);
    isExporting = false;
    filterValues = {
        search: '',
        status: 'all'
    };

    constructor(
        private zoneService: ZoneService,
        private dialog: MatDialog,
        private toastr: ToastrService
    ) {
    }

    ngAfterViewInit() {
        this.dataSource.paginator = this.paginator;
        this.paginator._intl.itemsPerPageLabel = 'Items por página:';
        this.paginator._intl.nextPageLabel = 'Siguiente';
        this.paginator._intl.previousPageLabel = 'Anterior';
    }

    ngOnInit(): void {
        this.loadZones();

        this.dataSource.filterPredicate = (data: ViolenceZoneInterface, filter: string) => {
            const searchTerms = JSON.parse(filter);
            const searchStr = (data.name + ' ' + (data.description || '')).toLowerCase();
            const matchesSearch = searchStr.includes(searchTerms.search.toLowerCase());

            let matchesStatus = true;
            if (searchTerms.status !== 'all') {
                const statusBool = searchTerms.status === 'true';
                matchesStatus = data.status === statusBool;
            }

            return matchesSearch && matchesStatus;
        };
    }

    loadZones(): void {
        this.zoneService.getAllZones().subscribe({
            next: (zone) => {
                this.zones = zone;
                this.dataSource.data = zone;
            },
            error: () => this.toastr.error('Error al cargar las zonas')
        });
    }

    openDialog(zone?: ViolenceZoneInterface): void {
        const dialogRef = this.dialog.open(ZoneFormDialog, {
            width: '850px',
            maxWidth: '95vw',
            maxHeight: '90vh',
            data: zone ? { ...zone } : null,
            disableClose: true,
            panelClass: 'zone-form-panel'
        });

        dialogRef.afterClosed().subscribe(result => {
            if (!result) return;

            if (zone && zone.id) {
                this.handleUpdate(zone.id, result);
            } else {
                this.handleCreate(result);
            }
        });
    }

    private handleCreate(data: any): void {
        this.zoneService.createZone(data).subscribe({
            next: () => {
                this.toastr.success('Nueva zona creada');
                this.loadZones();
            },
            error: () => this.toastr.error('Error al crear zona')
        });
    }

    private handleUpdate(id: number, data: any): void {
        const updateData = { id, ...data };

        this.zoneService.updateZone(id, updateData).subscribe({
            next: () => {
                this.toastr.success('Zona actualizada correctamente');
                this.loadZones();
            },
            error: (err) => {
                console.error(err);
                this.toastr.error('No se pudo actualizar');
            }
        });
    }

    deleteZone(id: number) {
        const dialogRef = this.dialog.open(ConfirmDialog, {
            width: '350px',
            data: { message: '¿Estás seguro de que deseas eliminar esta Zona? Esta acción no se puede deshacer.' }
        });
        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.zoneService.deleteZone(id).subscribe({
                    next: () => {
                        this.loadZones();
                        this.toastr.warning('La zona ha sido eliminada', 'Registro Borrado');
                    },
                    error: () => this.toastr.error('Hubo un problema al intentar eliminar', 'Error')
                });
            }
        });
    }

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

    filterByStatus(status: 'all' | 'true' | 'false'): void {
        this.filterValues.status = status;
        this.applyCombinedFilter();
    }

    viewDetail(zone: ViolenceZoneInterface): void {
        this.dialog.open(ZoneDetailDialog, {
            width: '600px',
            maxWidth: '95vw',
            data: zone,
            panelClass: 'zone-detail-panel'
        });
    }

    exportToExcel(): void {
        this.isExporting = true;
        const dataToExport = this.dataSource.data.map(inst => ({}));
        setTimeout(() => this.isExporting = false, 1000);

        const worksheet = XLSX.utils.json_to_sheet(dataToExport);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, 'Institutos');
        XLSX.writeFile(workbook, 'Reporte_Institutos.xlsx');
        this.toastr.info('Reporte generado correctamente');
    }

    getZoneCount(): number {
        return this.dataSource.data.length;
    }

    getActiveZoneCount(): number {
        return this.dataSource.data.filter(zone => zone.status).length;
    }

    getActivationRate(): number {
        if (!this.zones || this.zones.length === 0) return 0;
        const activeCount = this.zones.filter(z => z.status).length;
        return Math.round((activeCount / this.zones.length) * 100);
    }

    getMaxSeverityZone(): string {
        if (this.zones.length === 0) return 'N/A';
        const maxZone = [...this.zones].sort((a, b) => b.severity - a.severity)[0];
        return maxZone.name;
    }

    hasHighRiskConfigured(): boolean {
        return this.zones.some(z => z.severity >= 8 && z.status);
    }

    resetFilters(): void {
        this.filterValues = {
            search: '',
            status: 'all'
        };

        if (this.searchInput) {
            this.searchInput.nativeElement.value = '';
        }

        this.applyCombinedFilter();
        this.toastr.info('Filtros restablecidos');
    }
}