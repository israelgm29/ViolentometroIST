import { Component, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { FooterComponent } from '../../../shared/footer/footer.component';
import {ModalService} from "../../../services/modal.service";
import {QuizResultService} from "../../../services/quiz-result.service";
import {ResultsModalComponent} from "../../../components/results-modal/results-modal.component";

export interface EducationalInstitute {
    id: string;
    name: string;
    province: string;
    city: string;
    image?: string;
}

interface Resource {
    title: string;
    description: string;
    type: 'pdf' | 'video' | 'article';
    icon: string;
    url: string;
}

interface Testimonial {
    text: string;
    role: string;
}

@Component({
    selector: 'app-contact',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        MatIconModule,
        MatCardModule,
        MatButtonModule,
        FooterComponent,
        ResultsModalComponent
    ],
    templateUrl: './contact.component.html',
    styleUrls: ['./contact.component.scss']
})
export class ContactComponent {
    private  modalService = inject(ModalService);
    private quizResultService = inject(QuizResultService);
    currentTestimonialIndex: number = 0;

    // Signals para el modal de resultados
    showResultsModal = this.modalService.showResultsModal;
    resultData = computed(() => this.quizResultService.getResultData());



    resources: Resource[] = [
        {
            title: 'Guía de Señales de Violencia',
            description: 'Identifica los diferentes tipos de violencia y sus señales de alerta.',
            type: 'pdf',
            icon: 'picture_as_pdf',
            url: '#'
        },
        {
            title: 'Tus Derechos como Víctima',
            description: 'Conoce tus derechos legales y cómo ejercerlos.',
            type: 'pdf',
            icon: 'picture_as_pdf',
            url: '#'
        },
        {
            title: 'Video: Cómo Pedir Ayuda',
            description: 'Pasos seguros para buscar apoyo sin poner en riesgo tu seguridad.',
            type: 'video',
            icon: 'play_circle',
            url: '#'
        },
        {
            title: 'Plan de Seguridad Personal',
            description: 'Crea tu propio plan de seguridad en caso de emergencia.',
            type: 'article',
            icon: 'article',
            url: '#'
        },
        {
            title: 'Ciclo de la Violencia',
            description: 'Comprende las fases del ciclo de violencia y cómo romperlo.',
            type: 'article',
            icon: 'article',
            url: '#'
        },
        {
            title: 'Video: Recuperación y Sanación',
            description: 'Testimonios y consejos para el proceso de recuperación.',
            type: 'video',
            icon: 'play_circle',
            url: '#'
        }
    ];

    testimonials: Testimonial[] = [
        {
            text: 'Gracias a la ayuda que recibí, pude salir de una situación que parecía imposible. Hoy soy libre y feliz.',
            role: 'Sobreviviente anónima'
        },
        {
            text: 'El apoyo psicológico me ayudó a recuperar mi autoestima y a entender que no era mi culpa.',
            role: 'Persona en recuperación'
        },
        {
            text: 'La asesoría legal fue fundamental. Me acompañaron en todo el proceso y nunca me sentí sola.',
            role: 'Usuaria del servicio'
        },
        {
            text: 'Encontré un lugar seguro para mí y mis hijos. Ahora estamos construyendo una nueva vida.',
            role: 'Madre protegida'
        }
    ];

    educationalInstitutes: EducationalInstitute[] = [
        { id: '2367', name: 'CONSERVATORIO SUPERIOR DE MÚSICA JOSÉ MARÍA RODRÍGUEZ', province: 'AZUAY', city: 'CUENCA' },
        { id: '3010', name: 'INSTITUTO SUPERIOR TECNOLÓGICO DEL AZUAY', province: 'AZUAY', city: 'CUENCA' },
        { id: '2067', name: 'INSTITUTO SUPERIOR TECNOLÓGICO EL LIBERTADOR', province: 'BOLÍVAR', city: 'CHIMBO' },
        { id: '2007', name: 'INSTITUTO SUPERIOR TECNOLÓGICO ALFONSO HERRERA', province: 'CARCHI', city: 'ESPEJO' },
        { id: '2029', name: 'INSTITUTO SUPERIOR TECNOLÓGICO VICENTE FIERRO', province: 'CARCHI', city: 'TULCÁN' },
        { id: '2070', name: 'INSTITUTO SUPERIOR TECNOLÓGICO LA TRONCAL', province: 'CAÑAR', city: 'LA TRONCAL' },
        { id: '2138', name: 'INSTITUTO SUPERIOR TECNOLÓGICO DEL AUSTRO', province: 'CAÑAR', city: 'AZOGUES' },
        { id: '2363', name: 'INSTITUTO SUPERIOR PEDAGÓGICO INTERCULTURAL BILINGÜE QUILLOAC', province: 'CAÑAR', city: 'CAÑAR' },
        { id: '2001', name: 'INSTITUTO SUPERIOR TECNOLÓGICO CARLOS CISNEROS', province: 'CHIMBORAZO', city: 'RIOBAMBA' },
        { id: '2259', name: 'INSTITUTO SUPERIOR TECNOLÓGICO RIOBAMBA', province: 'CHIMBORAZO', city: 'RIOBAMBA' },
        { id: '0000', name: 'INSTITUTO SUPERIOR TECNOLÓGICO MANUEL GALECIO', province: 'CHIMBORAZO', city: 'ALAUSÍ' },
        { id: '2362', name: 'INSTITUTO PEDAGÓGICO INTERCULTURAL BILINGÜE JAIME ROLDÓS AGUILERA', province: 'CHIMBORAZO', city: 'COLTA' },
        { id: '2065', name: 'INSTITUTO SUPERIOR TECNOLÓGICO LA MANÁ', province: 'COTOPAXI', city: 'LA MANÁ' },
        { id: '2068', name: 'INSTITUTO SUPERIOR TECNOLÓGICO SIMÓN RODRÍGUEZ', province: 'COTOPAXI', city: 'LATACUNGA' },
        { id: '2071', name: 'INSTITUTO SUPERIOR TECNOLÓGICO VICENTE LEÓN', province: 'COTOPAXI', city: 'LATACUNGA' },
        { id: '3008', name: 'INSTITUTO SUPERIOR TECNOLÓGICO COTOPAXI', province: 'COTOPAXI', city: 'LATACUNGA' },
        { id: '2017', name: 'INSTITUTO SUPERIOR TECNOLÓGICO HUAQUILLAS', province: 'EL ORO', city: 'HUAQUILLAS' },
        { id: '2020', name: 'INSTITUTO SUPERIOR TECNOLÓGICO ISMAEL PÉREZ PAZMIÑO', province: 'EL ORO', city: 'MACHALA' },
        { id: '2027', name: 'INSTITUTO SUPERIOR TECNOLÓGICO OCHO DE NOVIEMBRE', province: 'EL ORO', city: 'PIÑAS' },
        { id: '2036', name: 'INSTITUTO SUPERIOR TECNOLÓGICO ALBERTO ENRÍQUEZ', province: 'IMBABURA', city: 'ATUNTAQUI' },
        { id: '0001', name: 'INSTITUTO SUPERIOR TECNOLÓGICO LUIS TELLO', province: 'ESMERALDAS', city: 'ESMERALDAS' },
        { id: '2255', name: 'INSTITUTO SUPERIOR TECNOLÓGICO QUININDÉ', province: 'ESMERALDAS', city: 'QUININDÉ' },
        { id: '2220', name: 'INSTITUTO SUPERIOR TECNOLÓGICO GALÁPAGOS', province: 'GALÁPAGOS', city: 'SANTA CRUZ' },
        { id: '2116', name: 'INSTITUTO SUPERIOR TECNOLÓGICO GUAYAQUIL', province: 'GUAYAS', city: 'GUAYAQUIL' },
        { id: '2118', name: 'INSTITUTO SUPERIOR TECNOLÓGICO JUAN BAUTISTA AGUIRRE', province: 'GUAYAS', city: 'DAULE' },
        { id: '2126', name: 'INSTITUTO SUPERIOR TECNOLÓGICO SIMÓN BOLÍVAR', province: 'GUAYAS', city: 'GUAYAQUIL' },
        { id: '2329', name: 'INSTITUTO SUPERIOR TECNOLÓGICO DE ARTES DEL ECUADOR (ITAE)', province: 'GUAYAS', city: 'GUAYAQUIL' },
        { id: '2371', name: 'INSTITUTO SUPERIOR TECNOLÓGICO VICENTE ROCAFUERTE', province: 'GUAYAS', city: 'GUAYAQUIL' },
        { id: '2038', name: 'INSTITUTO SUPERIOR TECNOLÓGICO COTACACHI', province: 'IMBABURA', city: 'COTACACHI' },
        { id: '2039', name: 'INSTITUTO SUPERIOR TECNOLÓGICO 17 DE JULIO', province: 'IMBABURA', city: 'IBARRA' },
        { id: '0002', name: 'INSTITUTO SUPERIOR TECNOLÓGICO DE ARTES PLÁSTICAS DANIEL REYES', province: 'IMBABURA', city: 'IBARRA' },
        { id: '2247', name: 'INSTITUTO SUPERIOR TECNOLÓGICO LUIS ULPIANO DE LA TORRE', province: 'IMBABURA', city: 'COTACACHI' },
        { id: '2047', name: 'INSTITUTO SUPERIOR TECNOLÓGICO CARIAMANGA', province: 'LOJA', city: 'CALVAS' },
        { id: '2289', name: 'CONSERVATORIO SUPERIOR SALVADOR BUSTAMANTE CELI', province: 'LOJA', city: 'LOJA' },
        { id: '3012', name: 'INSTITUTO SUPERIOR TECNOLÓGICO LOJA', province: 'LOJA', city: 'LOJA' },
        { id: '2208', name: 'INSTITUTO SUPERIOR TECNOLÓGICO BABAHOYO', province: 'LOS RÍOS', city: 'BABAHOYO' },
        { id: '2209', name: 'INSTITUTO SUPERIOR TECNOLÓGICO CIUDAD DE VALENCIA', province: 'LOS RÍOS', city: 'QUEVEDO' },
        { id: '2094', name: 'INSTITUTO SUPERIOR TECNOLÓGICO LUIS ARBOLEDA MARTÍNEZ', province: 'MANABÍ', city: 'MANTA' },
        { id: '2096', name: 'INSTITUTO SUPERIOR TECNOLÓGICO PAULO EMILIO MACÍAS', province: 'MANABÍ', city: 'PORTOVIEJO' },
        { id: '2280', name: 'INSTITUTO SUPERIOR TECNOLÓGICO LIMÓN', province: 'MORONA SANTIAGO', city: 'LIMÓN INDANZA' },
        { id: '2365', name: 'INSTITUTO PEDAGÓGICO INTERCULTURAL BILINGÜE SHUAR-ACHUAR', province: 'MORONA SANTIAGO', city: 'GUALAQUIZA' },
        { id: '3005', name: 'INSTITUTO SUPERIOR TECNOLÓGICO SUCÚA', province: 'MORONA SANTIAGO', city: 'SUCÚA' },
        { id: '2079', name: 'INSTITUTO SUPERIOR TECNOLÓGICO TENA', province: 'NAPO', city: 'TENA' },
        { id: '2053', name: 'INSTITUTO SUPERIOR TECNOLÓGICO GENERAL ELOY ALFARO', province: 'ORELLANA', city: 'LA JOYA DE LOS SACHAS' },
        { id: '2102', name: 'INSTITUTO SUPERIOR TECNOLÓGICO FRANCISCO DE ORELLANA', province: 'PASTAZA', city: 'PASTAZA' },
        { id: '2366', name: 'INSTITUTO PEDAGÓGICO INTERCULTURAL BILINGÜE CANELOS', province: 'PASTAZA', city: 'PASTAZA' },
        { id: '2183', name: 'INSTITUTO SUPERIOR TECNOLÓGICO LOS SHYRIS', province: 'PICHINCHA', city: 'QUITO' },
        { id: '2189', name: 'INSTITUTO SUPERIOR TECNOLÓGICO NELSON TORRES', province: 'PICHINCHA', city: 'CAYAMBE' },
        { id: '2192', name: 'INSTITUTO SUPERIOR TECNOLÓGICO POLICÍA NACIONAL', province: 'PICHINCHA', city: 'QUITO' },
        { id: '2200', name: 'INSTITUTO SUPERIOR TECNOLÓGICO SUCRE', province: 'PICHINCHA', city: 'QUITO' },
        { id: '2223', name: 'INSTITUTO SUPERIOR TECNOLÓGICO ARTESANAL', province: 'PICHINCHA', city: 'QUITO' },
        { id: '2239', name: 'INSTITUTO SUPERIOR TECNOLÓGICO CENTRAL TÉCNICO', province: 'PICHINCHA', city: 'QUITO' },
        { id: '2243', name: 'CONSERVATORIO SUPERIOR NACIONAL DE MÚSICA', province: 'PICHINCHA', city: 'QUITO' },
        { id: '3009', name: 'INSTITUTO SUPERIOR TECNOLÓGICO DE TURISMO Y PATRIMONIO YAVIRAC', province: 'PICHINCHA', city: 'QUITO' },
        { id: '2206', name: 'INSTITUTO SUPERIOR TECNOLÓGICO "CENTRO TECNOLÓGICO NAVAL"', province: 'SANTA ELENA', city: 'SALINAS' },
        { id: '3007', name: 'INSTITUTO SUPERIOR TECNOLÓGICO TSA\'CHILA', province: 'SANTO DOMINGO', city: 'SANTO DOMINGO' },
        { id: '2364', name: 'INSTITUTO SUPERIOR TECNOLÓGICO MARTHA BUCARAM DE ROLDÓS', province: 'SUCUMBÍOS', city: 'LAGO AGRIO' },
        { id: '2215', name: 'INSTITUTO SUPERIOR TECNOLÓGICO BOLÍVAR', province: 'TUNGURAHUA', city: 'AMBATO' },
        { id: '2229', name: 'INSTITUTO SUPERIOR TECNOLÓGICO TUNGURAHUA', province: 'TUNGURAHUA', city: 'AMBATO' },
        { id: '2231', name: 'INSTITUTO SUPERIOR TECNOLÓGICO PELILEO', province: 'TUNGURAHUA', city: 'PELILEO' },
        { id: '2082', name: 'INSTITUTO SUPERIOR TECNOLÓGICO AMAZÓNICO', province: 'ZAMORA CHINCHIPE', city: 'YANTZAZA' }
    ];

    // Filter State
    selectedCity: string = 'Todos';
    searchTerm: string = '';

    // Pagination State
    currentPage: number = 1;
    itemsPerPage: number = 9;

    // Derived Data: Unique Cities with Counts
    get cityFilters(): { name: string; count: number }[] {
        const cityCounts = new Map<string, number>();

        // Count institutes per city
        this.educationalInstitutes.forEach(edu => {
            const city = edu.city;
            cityCounts.set(city, (cityCounts.get(city) || 0) + 1);
        });

        // Convert map to array and sort alphabetically
        const cities = Array.from(cityCounts.entries()).map(([name, count]) => ({
            name,
            count
        })).sort((a, b) => a.name.localeCompare(b.name));

        // Prepend "Todos" option
        return [
            { name: 'Todos', count: this.educationalInstitutes.length },
            ...cities
        ];
    }

    get filteredEducationalInstitutes(): EducationalInstitute[] {
        let filtered = this.educationalInstitutes;

        // 1. Filter by City
        if (this.selectedCity !== 'Todos') {
            filtered = filtered.filter(edu => edu.city === this.selectedCity);
        }

        // 2. Filter by Search Term
        const term = this.searchTerm.toLowerCase().trim();
        if (term) {
            filtered = filtered.filter(edu =>
                edu.name.toLowerCase().includes(term) ||
                edu.province.toLowerCase().includes(term) ||
                edu.city.toLowerCase().includes(term) ||
                edu.id.includes(term)
            );
        }

        return filtered;
    }

    get paginatedEducationalInstitutes(): EducationalInstitute[] {
        const startIndex = (this.currentPage - 1) * this.itemsPerPage;
        return this.filteredEducationalInstitutes.slice(startIndex, startIndex + this.itemsPerPage);
    }

    get totalPages(): number {
        return Math.ceil(this.filteredEducationalInstitutes.length / this.itemsPerPage);
    }

    // Actions
    selectCity(city: string): void {
        this.selectedCity = city;
        this.currentPage = 1; // Reset to first page
        this.searchTerm = ''; // Optional: clear search on category switch? Maybe better not to.
        // Let's keep search independent or clear it. The user said "elige la ciudad y aparecen...".
        // Clearing search feels more natural when switching "folders".
    }

    setPage(page: number): void {
        if (page >= 1 && page <= this.totalPages) {
            this.currentPage = page;
            this.scrollToTop();
        }
    }

    onSearchChange(): void {
        this.currentPage = 1;
        if (this.searchTerm) {
            // If user searches globally, maybe reset city to Todos?
            // Or search within selected city?
            // "Buscar por nombre, provincia, ciudad..." implies global search.
            // If I search "Cuenca" while in "Quito" tab, I get 0 results.
            // To prevent confusion, if search term matches a city name, we could auto-switch?
            // Simpler: Search filters strictly within the CURRENT filtered set.
        }
    }

    getPagesArray(): number[] {
        const pages: number[] = [];
        const total = this.totalPages;
        if (total <= 0) return [];

        let start = Math.max(1, this.currentPage - 2);
        let end = Math.min(total, start + 4);

        if (end - start < 4) {
            start = Math.max(1, end - 4);
        }

        start = Math.max(1, start); // Ensure not negative

        for (let i = start; i <= end; i++) {
            pages.push(i);
        }
        return pages;
    }

    private scrollToTop(): void {
        // Optional: Implement smooth scroll to top of grid
    }

    get currentTestimonial(): Testimonial {
        return this.testimonials[this.currentTestimonialIndex];
    }

    openWhatsApp(phone: string): void {
        const message = encodeURIComponent('Hola, necesito información y ayuda.');
        const whatsappUrl = `https://wa.me/${phone}?text=${message}`;
        window.open(whatsappUrl, '_blank');
    }

    callPhone(phone: string): void {
        window.location.href = `tel:${phone}`;
    }

    activatePanicButton(): void {
        // Redirige a Google de forma discreta
        window.location.replace('https://www.google.com');
    }

    downloadResource(url: string): void {
        // En producción, esto descargaría el recurso real
        window.open(url, '_blank');
    }

    nextTestimonial(): void {
        this.currentTestimonialIndex = (this.currentTestimonialIndex + 1) % this.testimonials.length;
    }

    previousTestimonial(): void {
        this.currentTestimonialIndex = this.currentTestimonialIndex === 0
            ? this.testimonials.length - 1
            : this.currentTestimonialIndex - 1;
    }



    closeResultsModal(): void {
        this.modalService.closeResultsModal();
    }
}
