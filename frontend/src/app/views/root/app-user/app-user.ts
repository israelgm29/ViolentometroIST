import {Component, ElementRef, inject, ViewChild} from '@angular/core';
import {MatIconButton, MatButtonModule} from "@angular/material/button";
import {
    MatCell, MatCellDef, MatColumnDef,
    MatHeaderCell, MatHeaderCellDef,
    MatHeaderRow, MatHeaderRowDef,
    MatRow, MatRowDef, MatTable, MatTableDataSource
} from "@angular/material/table";
import {MatIcon} from "@angular/material/icon";
import {MatPaginator} from "@angular/material/paginator";
import {MatTab, MatTabGroup, MatTabLabel} from "@angular/material/tabs";
import {MatTooltip} from "@angular/material/tooltip";
import {NgTemplateOutlet} from "@angular/common";
import {AppUserRequest, AppUserResponse} from "../../../models/app-user";
import {ToastrService} from "ngx-toastr";
import {AppUserService} from "../../../services/app-user.service";
import {SysUserInterface} from "../../../models/sys-user";
import {MatDialog} from "@angular/material/dialog";
import {MatSort} from "@angular/material/sort";
import {AppUserFormDialog} from "../../../components/app-user-form-dialog/app-user-form-dialog";
import {AppUserDetailDialog} from "../../../components/app-user-detail-dialog/app-user-detail-dialog";
import {ConfirmDialog} from "../../../components/confirm-dialog/confirm-dialog";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatDivider} from "@angular/material/list";
import {BulkUploadService} from "../../../services/bulk-upload.service";
import {BulkUploadResultDialogComponent} from "../../../components/bulk-upload-result-dialog/bulk-upload-result-dialog";
import {
    BulkUploadConfirmDialogComponent
} from "../../../components/bulk-upload-confirm-dialog/bulk-upload-confirm-dialog";
import {AuthService} from "../../../services/auth.service";

@Component({
    selector: 'app-app-user',
    standalone: true,
    imports: [
        MatButtonModule,
        MatCell, MatCellDef, MatColumnDef,
        MatHeaderCell, MatHeaderRow, MatHeaderRowDef,
        MatIcon, MatIconButton,
        MatPaginator, MatRow, MatRowDef,
        MatTab, MatTabGroup, MatTabLabel,
        MatTable, MatTooltip,
        NgTemplateOutlet, MatHeaderCellDef,
        MatSort, MatMenuItem, MatMenu, MatMenuTrigger,
        MatDivider
    ],
    templateUrl: './app-user.html',
    styleUrl: './app-user.css',
})
export class AppUser {
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;
    @ViewChild('input') searchInput!: ElementRef;
    @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

    displayedColumns: string[] = ['dni', 'institute', 'gender', 'ethnicity', 'birthdate', 'actions'];
    dataSource = new MatTableDataSource<AppUserResponse>([]);

    currentStatusTab: boolean = true;
    searchValue: string = '';
    uploading = false;

    totalUsers = 0;
    activeUsers = 0;
    inactiveUsers = 0;
    private bulkUploadService = inject(BulkUploadService);
    authService = inject(AuthService);

    constructor(
        private appUserService: AppUserService,
        private toastr: ToastrService,
        private dialog: MatDialog
    ) {
    }

    ngOnInit(): void {
        this.loadUsers();
        this.setupFilterPredicate();
    }

    ngAfterViewInit() {
        this.dataSource.paginator = this.paginator;
    }

    loadUsers() {
        this.appUserService.getAppUsers().subscribe({
            next: (data) => {
                this.dataSource.data = data;
                this.dataSource.paginator = this.paginator;
                this.dataSource.sort = this.sort;

                // Calcular contadores
                this.totalUsers = data.length;
                this.activeUsers = data.filter(u => u.status).length;
                this.inactiveUsers = data.filter(u => !u.status).length;

                this.applyCombinedFilter();
            },
            error: () => this.toastr.error('Error al cargar los estudiantes', 'Error')
        });
    }


    // ── CARGA MASIVA ─────────────────────────────────────────
    onFileSelected(event: Event) {
        const input = event.target as HTMLInputElement;
        if (!input.files?.length) return;

        const file = input.files[0];
        input.value = '';

        const size = file.size < 1024 * 1024
            ? (file.size / 1024).toFixed(1) + ' KB'
            : (file.size / (1024 * 1024)).toFixed(1) + ' MB';

        const confirmRef = this.dialog.open(BulkUploadConfirmDialogComponent, {
            width: '500px',
            disableClose: true,
            data: {fileName: file.name, fileSize: size}
        });

        confirmRef.afterClosed().subscribe((confirmed: boolean) => {
            if (!confirmed) return;

            this.uploading = true;
            this.bulkUploadService.upload(file).subscribe({
                next: (result) => {
                    this.uploading = false;
                    this.loadUsers();
                    this.dialog.open(BulkUploadResultDialogComponent, {
                        width: '600px',
                        data: result,
                        disableClose: true
                    });
                },
                error: () => {
                    this.uploading = false;
                    this.toastr.error('Error al procesar el archivo', 'Error');
                }
            });
        });
    }

    // ── TABLA ────────────────────────────────────────────────
    calculateAge(birthday: Date): number {
        const timeDiff = Math.abs(Date.now() - new Date(birthday).getTime());
        return Math.floor((timeDiff / (1000 * 3600 * 24)) / 365.25);
    }

    onTabChange(event: any) {
        this.currentStatusTab = event.index === 0;
        this.applyCombinedFilter();
    }

    applyFilter(event: Event) {
        const filterValue = (event.target as HTMLInputElement).value;
        this.searchValue = filterValue;
        this.applyCombinedFilter();
        if (this.dataSource.paginator) {
            this.dataSource.paginator.firstPage();
        }
    }

    applyCombinedFilter() {
        this.dataSource.filter = JSON.stringify({
            text: this.searchValue || '',
            status: this.currentStatusTab
        });
    }

    setupFilterPredicate() {
        this.dataSource.filterPredicate = (data: AppUserResponse, filter: string) => {
            const searchTerms = JSON.parse(filter);
            const searchStr = `${data.gender.name} ${data.ethnicity.name} ${data.dni} ${data.institute?.name || ''}`.toLowerCase();
            const matchesSearch = searchStr.includes(searchTerms.text.toLowerCase());
            const matchesStatus = data.status === searchTerms.status;
            return matchesSearch && matchesStatus;
        };
    }

    resetFilters(): void {
        if (this.searchInput) {
            this.searchInput.nativeElement.value = '';
        }
        this.applyCombinedFilter();
        if (this.toastr) {
            this.toastr.info('Búsqueda restablecida');
        }
    }

    // ── DIALOGS ──────────────────────────────────────────────
    openDialog(user?: SysUserInterface): void {
        const dialogRef = this.dialog.open(AppUserFormDialog, {
            width: '100%', maxWidth: '700px',
            data: user || null, disableClose: true
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                if (user?.dni) this.updateAppUser(user.dni, result);
                else this.createAppUser(result);
            }
        });
    }

    private createAppUser(userData: AppUserRequest) {
        const newUser = {...userData, status: true};
        this.appUserService.createAppUser(newUser).subscribe({
            next: () => {
                this.toastr.success('Usuario creado exitosamente');
                this.loadUsers();
            },
            error: () => this.toastr.error('Error al crear el usuario')
        });
    }

    private updateAppUser(dni: string, userData: AppUserRequest) {
        this.appUserService.updateAppUser(dni, userData).subscribe({
            next: () => {
                this.toastr.success('Estudiante actualizado');
                this.loadUsers();
            },
            error: () => this.toastr.error('Error al actualizar')
        });
    }

    protected deleteAppUser(appUser: AppUserResponse) {
        const dialogRef = this.dialog.open(ConfirmDialog, {
            width: '350px',
            data: {
                title: 'Eliminar Registro',
                message: `¿Estás seguro de que deseas eliminar al usuario con DNI ${appUser.dni}? Esta acción no se puede deshacer.`
            }
        });

        dialogRef.afterClosed().subscribe(confirmed => {
            if (confirmed) {
                this.appUserService.deleteAppUser(appUser.id).subscribe({
                    next: () => {
                        this.toastr.success('Usuario eliminado');
                        this.loadUsers();
                    },
                    error: () => this.toastr.error('Error al eliminar usuario')
                });
            }
        });
    }

    toggleStatus(appUser: AppUserResponse): void {
        const isDeactivating = appUser.status;
        const dialogRef = this.dialog.open(ConfirmDialog, {
            width: '350px',
            data: {
                title: isDeactivating ? 'Desactivar Cuenta' : 'Activar Cuenta',
                message: isDeactivating
                    ? `¿Estás seguro de que deseas deshabilitar al usuario ${appUser.dni}?`
                    : `¿Deseas reactivar la cuenta de ${appUser.dni}?`
            }
        });

        dialogRef.afterClosed().subscribe(confirmed => {
            if (confirmed) {
                const newStatus = !appUser.status;
                this.appUserService.patchStatus(appUser.dni, newStatus).subscribe({
                    next: () => {
                        this.toastr.success(`Usuario ${newStatus ? 'activado' : 'desactivado'} con éxito`);
                        this.loadUsers();
                    },
                    error: () => this.toastr.error('Ocurrió un error al cambiar el estado')
                });
            }
        });
    }

    viewAppUserDetails(appUser: AppUserResponse): void {
        this.dialog.open(AppUserDetailDialog, {
            width: '100%',
            maxWidth: '700px',
            data: appUser,
            disableClose: false
        });
    }
}