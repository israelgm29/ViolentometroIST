import { Component, Input, Output, EventEmitter, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';
import { trigger, transition, style, animate } from '@angular/animations';
import { NgClass } from '@angular/common'; // Solo dejamos NgClass si lo usas para objetos complejos

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
    selector: 'app-results-modal',
    standalone: true,
    imports: [MatIconModule, MatButtonModule, NgClass],
    templateUrl: './results-modal.component.html',
    styleUrls: ['./results-modal.component.scss'],
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
export class ResultsModalComponent {
    @Input() resultData!: ResultData;
    @Input() show: boolean = true;
    @Output() close = new EventEmitter<void>();

    private router = inject(Router);

    closeModal() {
        this.close.emit();
    }

    navigateToContact() {
        this.router.navigate(['/welfare']);
        this.closeModal();
    }

    startNewQuiz() {
        // Lógica para reiniciar, usualmente redirigir a la ruta del test
        this.router.navigate(['/quiz']);
        this.closeModal();
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