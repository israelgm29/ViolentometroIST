import {Component, inject} from '@angular/core';
import {
    MatCell, MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef,
    MatTable,
    MatTableDataSource, MatTableModule
} from "@angular/material/table";
import {MasterCatalog} from "../../../models/app-user";
import {CatalogService} from "../../../services/catalog.service";
import {MatDialog} from "@angular/material/dialog";
import {MatIcon} from "@angular/material/icon";
import {MatTab, MatTabGroup} from "@angular/material/tabs";
import {MatButtonModule, MatIconButton} from "@angular/material/button";
import {MatCardModule} from "@angular/material/card";
import {CatalogFormDialogComponent} from "../../../components/catalog-form-dialog/catalog-form-dialog";
import {ConfirmDialog} from "../../../components/confirm-dialog/confirm-dialog"; // Asegúrate de importar tu diálogo
import {ToastrService} from "ngx-toastr";

@Component({
    selector: 'app-catalog-manager',
    imports: [
        MatIcon,
        MatTabGroup,
        MatButtonModule,
        MatTab,
        MatCardModule,
        MatTableModule,
        MatHeaderCellDef,
        MatColumnDef,
        MatHeaderCell,
        MatCell,
        MatIconButton,
        MatHeaderRow,
        MatRow,
        MatCellDef,
        MatHeaderRowDef,
        MatRowDef
    ],
    templateUrl: './catalog-manager.html',
    styleUrl: './catalog-manager.css',
})
export class CatalogManager {

    private catalogService = inject(CatalogService);
    private dialog = inject(MatDialog);
    private toastr = inject(ToastrService);

    paths = ['regions', 'disabilities', 'ethnicities', 'genders'];
    currentPath = 'regions';

    dataSource = new MatTableDataSource<MasterCatalog>([]);
    displayedColumns: string[] = ['name', 'actions'];

    ngOnInit(): void {
        this.loadData();
    }

    onTabChange(index: number): void {
        this.currentPath = this.paths[index];
        this.loadData();
    }

    loadData(): void {
        this.catalogService.findAll(this.currentPath).subscribe({
            next: (data) => {
                this.dataSource.data = data;
            },
            error: (err) => {
                this.toastr.error('Error al cargar los datos', 'Error');
                console.error(err);
            }
        });
    }

    openDialog(element?: MasterCatalog): void {
        const isEditMode = !!element;

        const dialogRef = this.dialog.open(CatalogFormDialogComponent, {
            width: '350px',
            data: element ? {...element} : {name: ''}
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                // Si tiene id, es edición; si no, es creación
                if (isEditMode && element?.id) {
                    // Actualizar
                    this.catalogService.update(this.currentPath, element.id, result).subscribe({
                        next: () => {
                            this.loadData();
                            this.toastr.success('Registro actualizado correctamente', 'Actualizado');
                        },
                        error: (err) => {
                            this.toastr.error('Error al actualizar el registro', 'Error');
                            console.error(err);
                        }
                    });
                } else {
                    // Crear nuevo
                    this.catalogService.save(this.currentPath, result).subscribe({
                        next: () => {
                            this.loadData();
                            this.toastr.success('Registro creado correctamente', 'Creado');
                        },
                        error: (err) => {
                            this.toastr.error('Error al crear el registro', 'Error');
                            console.error(err);
                        }
                    });
                }
            }
        });
    }

    deleteElement(id: number): void {
        const dialogRef = this.dialog.open(ConfirmDialog, {
            width: '350px',
            data: {
                message: '¿Estás seguro de que deseas eliminar este registro? Esta acción no se puede deshacer.'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.catalogService.delete(this.currentPath, id).subscribe({
                    next: () => {
                        this.loadData();
                        this.toastr.warning('El registro ha sido eliminado', 'Registro Borrado');
                    },
                    error: (err) => {
                        this.toastr.error('Hubo un problema al intentar eliminar', 'Error');
                        console.error(err);
                    }
                });
            }
        });
    }
}