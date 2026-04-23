import {Component, signal, computed, OnInit, inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatIconModule} from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatChipsModule} from '@angular/material/chips';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatDividerModule} from '@angular/material/divider';
import {MatTooltipModule} from '@angular/material/tooltip';
import {animate, style, transition, trigger} from '@angular/animations';
import {FooterComponent} from '../../../shared/footer/footer.component';
import {CampaignService} from '../../../services/campaign.service';
import {CampaignCategory, CampaignDTO} from "../../../models/campañing";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
    selector: 'app-campaigns',
    standalone: true,
    imports: [
        CommonModule,
        MatIconModule,
        MatCardModule,
        MatButtonModule,
        MatChipsModule,
        MatProgressBarModule,
        MatDividerModule,
        MatTooltipModule,
        FooterComponent
    ],
    templateUrl: './campaigns.component.html',
    styleUrls: ['./campaigns.component.scss'],
    animations: [
        trigger('fadeIn', [
            transition(':enter', [
                style({opacity: 0, transform: 'translateY(20px)'}),
                animate('0.5s ease-out', style({opacity: 1, transform: 'translateY(0)'}))
            ])
        ]),
        trigger('cardEnter', [
            transition(':enter', [
                style({opacity: 0, transform: 'translateY(30px)'}),
                animate('0.5s cubic-bezier(0.4, 0, 0.2, 1)',
                    style({opacity: 1, transform: 'translateY(0)'}))
            ])
        ])
    ]
})
export class CampaignsComponent implements OnInit {
    private campaignService = inject(CampaignService);
    private snackBar = inject(MatSnackBar);

    campaigns = signal<CampaignDTO[]>([]);
    categories = signal<CampaignCategory[]>([]);
    activeCategory = signal<string>('Todos');
    loading = signal(true);

    ngOnInit() {
        this.loadData();
    }

    loadData() {
        this.loading.set(true);
        this.campaignService.getActive().subscribe({
            next: (data) => {
                this.campaigns.set(data);
                this.loading.set(false);
            },
            error: () => this.loading.set(false)
        });

        this.campaignService.getActiveCategories().subscribe({
            next: (cats) => this.categories.set(cats)
        });
    }

    categoryNames = computed(() => [
        'Todos',
        ...this.categories().map(c => c.name)
    ]);

    filteredCampaigns = computed(() => {
        const cat = this.activeCategory();
        const all = this.campaigns();
        if (cat === 'Todos') return all;
        return all.filter(a => a.categoryName === cat);
    });

    featuredCampaign = computed(() =>
        this.campaigns().find(c => c.featured) ?? this.campaigns()[0] ?? null
    );

    regularCampaigns = computed(() =>
        this.filteredCampaigns().filter(c => !c.featured || this.activeCategory() !== 'Todos')
    );

    setCategory(cat: string) {
        this.activeCategory.set(cat);
    }

    openCampaign(campaign: CampaignDTO) {
        if (campaign.externalUrl) {
            window.open(campaign.externalUrl, '_blank');
        } else {
            this.snackBar.open('Esta publicación no tiene un enlace externo configurado.', 'Cerrar', {
                duration: 3000
            });
        }
    }

    getCategoryColor(categoryName?: string): string {
        if (!categoryName) return '#8B5CF6';
        return this.categories().find(c => c.name === categoryName)?.color ?? '#8B5CF6';
    }

    getCategoryIcon(categoryName?: string): string {
        if (!categoryName) return 'campaign';
        return this.categories().find(c => c.name === categoryName)?.icon ?? 'campaign';
    }

    formatDate(dateStr?: string): string {
        if (!dateStr) return '';
        return new Date(dateStr).toLocaleDateString('es-EC', {
            day: '2-digit', month: 'short', year: 'numeric'
        });
    }
}