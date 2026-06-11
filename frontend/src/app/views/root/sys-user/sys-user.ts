import {Component, ElementRef, ViewChild} from '@angular/core';
import {SysUserService} from "../../../services/sys-user.service";
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {ToastrService} from "ngx-toastr";
import {MatPaginator, MatPaginatorModule} from "@angular/material/paginator";
import {MatCardModule} from "@angular/material/card";
import {MatIcon, MatIconModule} from "@angular/material/icon";
import {
    MatTableDataSource,
    MatTableModule
} from "@angular/material/table";
import {SysUserInterface} from "../../../models/sys-user";
import {MatButtonModule} from "@angular/material/button";
import {UserFormDialog} from "../../../components/user-form-dialog/user-form-dialog";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatTabsModule} from "@angular/material/tabs";
import {MatInputModule} from "@angular/material/input";
import {CommonModule} from "@angular/common";
import {MatSort, MatSortModule} from "@angular/material/sort";
import {FormsModule} from "@angular/forms";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatMenuModule} from "@angular/material/menu";
import {ConfirmDialog} from "../../../components/confirm-dialog/confirm-dialog";
import {UserDetailDialog} from "../../../components/user-detail-dialog/user-detail-dialog";
import {ResetPasswordDialog} from "../../../shared/admin/reset-password-dialog/reset-password-dialog";

@Component({
    selector: 'app-sys-user',
    standalone: true,
    imports: [
        CommonModule, FormsModule, MatTableModule, MatPaginatorModule,
        MatSortModule, MatTabsModule, MatCardModule, MatFormFieldModule,
        MatInputModule, MatButtonModule, MatIcon, MatTooltipModule,
        MatMenuModule, MatDialogModule
    ],
    templateUrl: './sys-user.html',
    styleUrl: './sys-user.scss',
})
export class SysUser {
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort
    @ViewChild('input') searchInput!: ElementRef;
    displayedColumns: string[] = ['user', 'role', 'institute', 'contact', 'actions'];
    dataSource = new MatTableDataSource<SysUserInterface>([]);

    currentStatusTab: boolean = true;
    searchValue: string = '';

    constructor(
        private sysUserService: SysUserService,
        private dialog: MatDialog,
        private toastr: ToastrService) {
    }

    ngOnInit() {
        this.loadSysUsers();
        this.setupFilterPredicate();
    }

    ngAfterViewInit() {
        this.dataSource.paginator = this.paginator;
    }

    loadSysUsers() {
        this.sysUserService.getSysUsers().subscribe({
            next: (data) => {
                this.dataSource.data = data;
                this.dataSource.paginator = this.paginator;
                this.dataSource.sort = this.sort;
                // Aplicar filtro inicial para mostrar solo activos
                this.applyCombinedFilter();
            },
            error: (error) => {
                this.toastr.error('Error loading system users', 'Error');
            }
        });
    }

    setupFilterPredicate() {
        this.dataSource.filterPredicate = (data: SysUserInterface, filter: string) => {
            const searchTerms = JSON.parse(filter);

            // Filtro por texto
            const searchStr = `${data.firstname} ${data.firstLastname} ${data.dni} ${data.institute?.name || ''}`.toLowerCase();
            const matchesSearch = searchStr.includes(searchTerms.text.toLowerCase());

            // Filtro por estado (el de la pestaña actual)
            const matchesStatus = data.status === searchTerms.status;

            return matchesSearch && matchesStatus;
        };
    }

    openDialog(user?: SysUserInterface): void {
        const dialogRef = this.dialog.open(UserFormDialog, {
            width: '100%',
            maxWidth: '700px',
            data: user || null,
            disableClose: true
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                if (user?.id) {
                    this.updateUser(user.id, result);
                } else {
                    this.createUser(result);
                }
            }
        });
    }

    onTabChange(event: any) {
        this.currentStatusTab = event.index === 0; // Pestaña 0 es Activos
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

    getAdminCount(): number {
        return this.dataSource.data.filter(u =>
            u.role.name.toLowerCase().includes('admin') && u.status === true
        ).length;
    }

    applyCombinedFilter() {
        this.dataSource.filter = JSON.stringify({
            text: this.searchValue || '', // Lo que haya en el input de búsqueda
            status: this.currentStatusTab
        });
    }

    private createUser(userData: SysUserInterface) {
        const newUser = { ...userData, status: true };
        this.sysUserService.saveUser(newUser).subscribe({
             next: () => {
                 this.toastr.success('Usuario creado exitosamente');
                 this.loadSysUsers();
             },
             error: () => this.toastr.error('Error al crear el usuario')
         });
    }

    private updateUser(id: number, userData: any) {
        this.sysUserService.updateSysUser(id, userData).subscribe({
             next: () => {
                 this.toastr.success('Usuario actualizado');
                 this.loadSysUsers();
             },
             error: () => this.toastr.error('Error al actualizar')
         });
    }

    toggleStatus(user: SysUserInterface): void {
        const isDeactivating = user.status; // Si está true, lo vamos a desactivar
        const title = isDeactivating ? 'Desactivar Cuenta' : 'Activar Cuenta';
        const message = isDeactivating
            ? `¿Estás seguro de que deseas deshabilitar al usuario ${user.firstname}? No podrá acceder al sistema.`
            : `¿Deseas reactivar la cuenta de ${user.firstname}?`;

        // Abrimos el diálogo de confirmación
        const dialogRef = this.dialog.open(ConfirmDialog, {
            width: '350px',
            data: { title, message }
        });

        dialogRef.afterClosed().subscribe(confirmed => {
            if (confirmed) {
                const newStatus = !user.status;
                const actionText = newStatus ? 'activado' : 'desactivado';

                this.sysUserService.patchStatus(user.id, newStatus).subscribe({
                    next: () => {
                        this.toastr.success(`Usuario ${actionText} con éxito`);
                        this.loadSysUsers();
                    },
                    error: () => this.toastr.error('Ocurrió un error al cambiar el estado')
                });
            }
        });
    }

    resetFilters(): void {

        if (this.searchInput) {
            this.searchInput.nativeElement.value = '';
        }

        this.applyCombinedFilter();

        // 4. Mensaje de confirmación (si usas toastr)
        if (this.toastr) {
            this.toastr.info('Búsqueda restablecida');
        }
    }

    viewUserDetails(user: SysUserInterface): void {
        this.dialog.open(UserDetailDialog, {
            width: '100%',
            maxWidth: '700px',
            data: user,
            disableClose: false
        });
    }

    openResetPassword(user: SysUserInterface) {
        const dialogRef = this.dialog.open(ResetPasswordDialog, {
            width: '400px',
            data: { name: `${user.firstname} ${user.firstLastname}` }
        });

        dialogRef.afterClosed().subscribe(newPassword => {
            if (newPassword) {
                this.sysUserService.resetPassword(user.id, newPassword).subscribe({
                    next: () => {
                        this.toastr.success('Contraseña actualizada correctamente', 'Éxito');
                    },
                    error: (err) => {
                        this.toastr.error('No se pudo actualizar la contraseña', 'Error');
                    }
                });
            }
        });
    }
}