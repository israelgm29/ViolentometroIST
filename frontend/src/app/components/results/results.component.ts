import { Component, Input, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';
import { trigger, transition, style, animate } from '@angular/animations';
import {NgClass, TitleCasePipe} from '@angular/common';

export interface ResultData {
    nivel: 'bajo' | 'medio' | 'alto' | 'critico';
    titulo: string;
    mensaje: string;
    icon: string;
    recomendaciones: string[];
    colorClass: string;
    image?: string;
}

@Component({
    selector: 'app-results',
    standalone: true,
    imports: [MatIconModule, MatButtonModule, NgClass, TitleCasePipe],
    templateUrl: './results.component.html',
    styleUrls: ['./results.component.scss'],
    animations: [
        trigger('fadeIn', [
            transition(':enter', [
                style({ opacity: 0 }),
                animate('300ms ease-out', style({ opacity: 1 }))
            ]),
            transition(':leave', [
                animate('200ms ease-in', style({ opacity: 0 }))
            ])
        ]),
        trigger('slideIn', [
            transition(':enter', [
                style({ transform: 'translateY(-24px) scale(0.97)', opacity: 0 }),
                animate('380ms cubic-bezier(0.4, 0, 0.2, 1)',
                    style({ transform: 'translateY(0) scale(1)', opacity: 1 }))
            ])
        ])
    ]
})
export class ResultsComponent {
    @Input() resultData!: ResultData;

    private router = inject(Router);

    closeModal() {
       //pendiente
    }

    navigateToContact() {
        this.router.navigate(['/welfare']);
    }

    startNewQuiz() {
        window.location.reload();
    }

    openWhatsapp() {
        const phoneNumber = '593998792631';
        const message = ` Hola, mi resultado en Violentometro fue: ${this.resultData.mensaje} Necesito orientación.`;
        window.open(`https://wa.me/${phoneNumber}?text=${encodeURIComponent(message)}`, '_blank');
    }

    openGoogleForm() {
        window.open('https://docs.google.com/forms/u/0/', '_blank');
    }
}