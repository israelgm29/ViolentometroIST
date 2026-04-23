import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-floating-whatsapp',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './floating-whatsapp.component.html',
    styleUrl: './floating-whatsapp.component.scss'
})
export class FloatingWhatsappComponent {
    whatsappNumber: string = '593999999999'; // Replace with actual number if provided or keep placeholder
    message: string = 'Hola, necesito ayuda.';

    openWhatsapp() {
        const url = `https://wa.me/${this.whatsappNumber}?text=${encodeURIComponent(this.message)}`;
        window.open(url, '_blank');
    }
}
