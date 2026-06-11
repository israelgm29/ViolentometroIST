import {
    Component,
    OnInit,
    signal,
    computed,
    inject,
    ViewChild,
    AfterViewInit,
    ChangeDetectionStrategy
} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {MatTableModule, MatTableDataSource} from '@angular/material/table';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatMenuModule} from '@angular/material/menu';
import {MatPaginator, MatPaginatorModule} from '@angular/material/paginator';
import {MatDividerModule} from '@angular/material/divider';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatDialog} from '@angular/material/dialog';
import {ToastrService} from 'ngx-toastr';

import {CampaignService} from '../../../services/campaign.service';
import {CampaignDTO, CampaignCategory} from '../../../models/campañing';
import {ConfirmDialog} from '../../../components/confirm-dialog/confirm-dialog';
import {CampaingFormComponent} from "../../../components/campaing-form-dialog/campaing-form-dialog";
import {CampaingCategoryForm} from "../../../components/campaing-category-form/campaing-category-form";
import {CampaignPreviewDialogComponent} from "../../../components/campaing-preview-dialog/campaing-preview-dialog";


type FilterType = 'all' | 'active' | 'draft' | 'featured';
type ViewType = 'list' | 'grid';

interface FilterOption {
    value: FilterType;
    label: string;
    icon: string;
}

interface StatWidget {
    label: string;
    icon: string;
    iconClass: string;
    value: () => number;
    trendIcon: string;
    trendText: string;
    trendClass: string;
}

@Component({
    selector: 'app-campaing',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        MatTableModule,
        MatIconModule,
        MatButtonModule,
        MatMenuModule,
        MatPaginatorModule,
        MatDividerModule,
        MatTooltipModule,
    ],
    templateUrl: './campaing.html',
    styleUrls: ['./campaing.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CampaingComponent implements OnInit, AfterViewInit {

    private campaignService = inject(CampaignService);
    private dialog = inject(MatDialog);
    private toastr = inject(ToastrService);

    campaigns = signal<CampaignDTO[]>([]);
    categories = signal<CampaignCategory[]>([]);
    loading = signal(false);
    searchTerm = signal('');
    currentFilter = signal<FilterType>('all');
    viewMode = signal<ViewType>('list');

    // Table
    displayedColumns: string[] = ['title', 'date', 'category', 'status', 'actions'];
    dataSource = new MatTableDataSource<CampaignDTO>([]);

    @ViewChild(MatPaginator) paginator!: MatPaginator;

    // Filtros
    readonly filters: FilterOption[] = [
        {value: 'all', label: 'Todas', icon: 'apps'},
        {value: 'active', label: 'En línea', icon: 'visibility'},
        {value: 'draft', label: 'Borradores', icon: 'edit'},
        {value: 'featured', label: 'Destacadas', icon: 'star'}
    ];

    // Stats
    stats = computed<StatWidget[]>(() => [
        {
            label: 'Total campañas',
            icon: 'campaign',
            iconClass: 'icon-purple',
            value: () => this.totalCount(),
            trendIcon: 'trending_up',
            trendText: 'Activas',
            trendClass: ''
        },
        {
            label: 'En línea',
            icon: 'visibility',
            iconClass: 'icon-green',
            value: () => this.activeCount(),
            trendIcon: 'check_circle',
            trendText: this.totalCount() > 0
                ? `${Math.round((this.activeCount() / this.totalCount()) * 100)}%`
                : '0%',
            trendClass: 'positive'
        },
        {
            label: 'Destacadas',
            icon: 'star',
            iconClass: 'icon-amber',
            value: () => this.featuredCount(),
            trendIcon: 'auto_awesome',
            trendText: 'Prioridad',
            trendClass: 'featured'
        },
        {
            label: 'Borrador',
            icon: 'visibility_off',
            iconClass: 'icon-slate',
            value: () => this.draftCount(),
            trendIcon: 'edit_note',
            trendText: 'Pendientes',
            trendClass: 'draft'
        }
    ]);

    // Computed
    totalCount = computed(() => this.campaigns().length);
    activeCount = computed(() => this.campaigns().filter(c => c.status).length);
    featuredCount = computed(() => this.campaigns().filter(c => c.featured).length);
    draftCount = computed(() => this.campaigns().filter(c => !c.status).length);

    filteredCampaigns = computed(() => {
        let filtered = this.campaigns();
        const filter = this.currentFilter();

        switch (filter) {
            case 'active':
                filtered = filtered.filter(c => c.status);
                break;
            case 'draft':
                filtered = filtered.filter(c => !c.status);
                break;
            case 'featured':
                filtered = filtered.filter(c => c.featured);
                break;
        }

        return filtered;
    });

    ngOnInit() {
        this.loadData();
    }

    ngAfterViewInit() {
        this.dataSource.paginator = this.paginator;
    }

    loadData() {
        this.loading.set(true);

        this.campaignService.getAll().subscribe({
            next: (data) => {
                this.campaigns.set(data);
                this.updateDataSource();
                this.loading.set(false);
            },
            error: () => {
                this.toastr.error('Error al cargar las campañas', 'Error');
                this.loading.set(false);
            }
        });

        this.campaignService.getAllCategories().subscribe({
            next: (cats) => this.categories.set(cats)
        });
    }

    updateDataSource() {
        this.dataSource.data = this.filteredCampaigns();
    }


    openPreview(campaign: CampaignDTO, event?: MouseEvent) {
        if (event) {
            const target = event.target as HTMLElement;
            const button = target.closest('button');
            button?.blur();
        }

        setTimeout(() => {
            (document.activeElement as HTMLElement)?.blur();
        }, 0);

        const enrichedCampaign: CampaignDTO = {
            ...campaign,
            categoryColor: this.getCategoryColor(campaign.categoryName),
            categoryIcon: this.getCategoryIcon(campaign.categoryName)
        };

        const dialogRef = this.dialog.open(CampaignPreviewDialogComponent, {
            data: enrichedCampaign,
            width: '800px',
            maxWidth: '95vw',
            maxHeight: '90vh',
            panelClass: 'preview-dialog-panel',
            autoFocus: false,
            restoreFocus: false
        });

        dialogRef.afterClosed().subscribe(result => {
            if (!result) return;

            if (result.action === 'edit') {
                this.openForm(result.campaign);
            }
        });
    }

    publishFromPreview(id: number | undefined) {
        if (!id) {
            this.toastr.error('ID no válido', 'Error');
            return;
        }

        const campaign = this.campaigns().find(c => c.id === id);
        if (!campaign) return;

        const ref = this.dialog.open(ConfirmDialog, {
            width: '380px',
            data: {
                title: 'Publicar campaña',
                message: `¿Deseas publicar "${campaign.title}" ahora?`,
                confirmButton: 'Publicar',
                confirmColor: 'primary'
            }
        });

        ref.afterClosed().subscribe(confirmed => {
            if (!confirmed) return;

            this.campaignService.toggleStatus(id).subscribe({
                next: () => {
                    this.campaigns.update(list =>
                        list.map(c => c.id === id ? {...c, status: true} : c)
                    );
                    this.updateDataSource();
                    this.toastr.success('Campaña publicada correctamente', 'Éxito');
                },
                error: () => this.toastr.error('Error al publicar', 'Error')
            });
        });
    }

    // ========== FILTROS Y BÚSQUEDA ==========

    setFilter(filter: FilterType) {
        this.currentFilter.set(filter);
        this.updateDataSource();
        this.paginator?.firstPage();
    }

    setViewMode(mode: ViewType) {
        this.viewMode.set(mode);
    }

    updateSearch(event: Event) {
        const value = (event.target as HTMLInputElement).value;
        this.searchTerm.set(value);
        this.dataSource.filter = value.trim().toLowerCase();
        this.paginator?.firstPage();
    }

    clearSearch() {
        this.searchTerm.set('');
        this.dataSource.filter = '';
        this.paginator?.firstPage();
    }

    // ========== FORMULARIOS ==========

    openForm(campaign?: CampaignDTO) {
        const dialogRef = this.dialog.open(CampaingFormComponent, {
            width: '900px',
            maxWidth: '95vw',
            maxHeight: '90vh',
            disableClose: true,
            panelClass: 'custom-dialog',
            data: campaign ?? null
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                const message = campaign
                    ? 'Campaña actualizada correctamente'
                    : 'Campaña creada correctamente';
                this.toastr.success(message, 'Éxito');
                this.loadData();
            }
        });
    }

    openCategoryForm() {
        const dialogRef = this.dialog.open(CampaingCategoryForm, {
            width: '500px',
            maxWidth: '95vw',
            disableClose: true,
            panelClass: 'custom-dialog'
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.toastr.success('Categorías actualizadas', 'Éxito');
                this.loadData();
            }
        });
    }

    // Acciones de campaña
    toggleStatus(id: number) {
        const campaign = this.campaigns().find(c => c.id === id);
        if (!campaign) return;

        const newStatus = !campaign.status;
        const successText = newStatus ? 'publicada' : 'ocultada';

        const ref = this.dialog.open(ConfirmDialog, {
            width: '380px',
            data: {
                title: newStatus ? 'Publicar campaña' : 'Ocultar campaña',
                message: `¿Deseas ${newStatus ? 'publicar' : 'ocultar'} la campaña "${campaign.title}"?${newStatus ? ' Estará visible para todos los usuarios.' : ''}`,
                confirmButton: newStatus ? 'Publicar' : 'Ocultar',
                confirmColor: newStatus ? 'primary' : undefined
            }
        });

        ref.afterClosed().subscribe(confirmed => {
            if (!confirmed) return;

            this.campaignService.toggleStatus(id).subscribe({
                next: () => {
                    this.campaigns.update(list =>
                        list.map(c => c.id === id ? {...c, status: newStatus} : c)
                    );
                    this.updateDataSource();
                    this.toastr.success(`Campaña ${successText}`, 'Éxito');
                },
                error: () => this.toastr.error('Error al cambiar el estado', 'Error')
            });
        });
    }

    toggleFeatured(campaign: CampaignDTO) {

        if (campaign.id === undefined) {
            this.toastr.error('No se puede actualizar una campaña sin ID', 'Error');
            return;
        }

        const newFeatured = !campaign.featured;
        this.campaignService.update(campaign.id!, {...campaign, featured: newFeatured}).subscribe({
            next: () => {
                this.campaigns.update(list =>
                    list.map(c => c.id === campaign.id ? {...c, featured: newFeatured} : c)
                );
                this.updateDataSource();
                this.toastr.success(
                    `Campaña ${newFeatured ? 'destacada' : 'quitada de destacados'}`,
                    'Éxito',
                    {timeOut: 2000}
                );
            },
            error: () => this.toastr.error('Error al actualizar', 'Error')
        });
    }

    deleteCampaign(id: number | undefined) {
        if (!id) {
            this.toastr.error('ID no válido', 'Error');
            return;
        }

        const campaign = this.campaigns().find(c => c.id === id);
        if (!campaign) return;

        const ref = this.dialog.open(ConfirmDialog, {
            width: '400px',
            data: {
                title: 'Eliminar campaña',
                message: `¿Estás seguro de que deseas eliminar "${campaign.title}"?`,
                warning: 'Esta acción no se puede deshacer.',
                confirmButton: 'Eliminar',
                confirmColor: 'warn',
                icon: 'delete_forever'
            }
        });

        ref.afterClosed().subscribe(confirmed => {
            if (!confirmed) return;

            this.campaignService.delete(id).subscribe({
                next: () => {
                    this.campaigns.update(list => list.filter(c => c.id !== id));
                    this.updateDataSource();
                    this.toastr.success('Campaña eliminada permanentemente', 'Éxito');
                },
                error: () => this.toastr.error('Error al eliminar la campaña', 'Error')
            });
        });
    }

    duplicateCampaign(campaign: CampaignDTO) {
        if (!campaign.id) {
            this.toastr.error('No se puede duplicar', 'Error');
            return;
        }

        const duplicated: Omit<CampaignDTO, 'id'> = {
            title: `${campaign.title} (Copia)`,
            excerpt: campaign.excerpt,
            imageUrl: campaign.imageUrl,
            externalUrl: campaign.externalUrl,
            featured: false,
            status: false,
            publishDate: new Date().toISOString(),
            startDate: campaign.startDate,
            endDate: campaign.endDate,
            categoryId: campaign.categoryId,
            categoryName: campaign.categoryName,
            categoryColor: campaign.categoryColor,
            categoryIcon: campaign.categoryIcon,
            createdAt: new Date().toISOString()
        };

        this.campaignService.create(duplicated).subscribe({
            next: () => {
                this.toastr.success('Campaña duplicada correctamente', 'Éxito');
                this.loadData();
            },
            error: () => this.toastr.error('Error al duplicar', 'Error')
        });
    }

    // ========== HELPERS ==========

    getCategoryColor(categoryName?: string): string {
        if (!categoryName) return '#6d28d9';
        return this.categories().find(c => c.name === categoryName)?.color ?? '#6d28d9';
    }

    getCategoryIcon(categoryName?: string): string {
        if (!categoryName) return 'campaign';
        return this.categories().find(c => c.name === categoryName)?.icon ?? 'campaign';
    }

    formatDate(dateStr?: string): string {
        if (!dateStr) return '—';

        const date = new Date(dateStr);
        const today = new Date();
        const tomorrow = new Date(today);
        tomorrow.setDate(tomorrow.getDate() + 1);

        const dateOnly = new Date(date.getFullYear(), date.getMonth(), date.getDate());
        const todayOnly = new Date(today.getFullYear(), today.getMonth(), today.getDate());
        const tomorrowOnly = new Date(tomorrow.getFullYear(), tomorrow.getMonth(), tomorrow.getDate());

        if (dateOnly.getTime() === todayOnly.getTime()) return 'Hoy';
        if (dateOnly.getTime() === tomorrowOnly.getTime()) return 'Mañana';

        return date.toLocaleDateString('es-EC', {
            day: '2-digit',
            month: 'short',
            year: 'numeric'
        });
    }
}