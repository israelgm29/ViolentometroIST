import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { FooterComponent } from '../../../shared/footer/footer.component';

@Component({
    selector: 'app-about',
    standalone: true,
    imports: [CommonModule, MatIconModule],
    templateUrl: './about.component.html',
    styleUrls: ['./about.component.scss']
})
export class AboutComponent {
    objectives = [
        {
            icon: 'visibility',
            title: 'Visibilizar',
            description: 'Hacer visible las diferentes formas de violencia en las relaciones de pareja.'
        },
        {
            icon: 'school',
            title: 'Educar',
            description: 'Proporcionar información y herramientas para identificar relaciones saludables.'
        },
        {
            icon: 'support',
            title: 'Apoyar',
            description: 'Ofrecer recursos y acompañamiento a quienes lo necesiten.'
        },
        {
            icon: 'security',
            title: 'Prevenir',
            description: 'Promover relaciones basadas en el respeto y la igualdad.'
        }
    ];

    violenceLevels = [
        {
            level: 'Fase 1',
            color: '#4ade80',
            icon: 'sentiment_satisfied',
            title: 'Relación Saludable',
            description: 'Respeto mutuo, comunicación abierta y apoyo.'
        },
        {
            level: 'Fase 2',
            color: '#fbbf24',
            icon: 'warning',
            title: 'Señales de Alerta',
            description: 'Celos, control, críticas constantes.'
        },
        {
            level: 'Fase 3',
            color: '#fb923c',
            icon: 'error',
            title: 'Violencia Moderada',
            description: 'Aislamiento, amenazas, manipulación emocional.'
        },
        {
            level: 'Fase 4',
            color: '#ef4444',
            icon: 'dangerous',
            title: 'Violencia Grave',
            description: 'Violencia física, sexual o psicológica severa.'
        }
    ];

    features = [
        {
            icon: 'quiz',
            title: 'Cuestionario Interactivo',
            description: 'Evalúa tu relación de forma anónima y confidencial.'
        },
        {
            icon: 'analytics',
            title: 'Resultados Personalizados',
            description: 'Recibe recomendaciones específicas según tu situación.'
        },
        {
            icon: 'contact_support',
            title: 'Recursos de Ayuda',
            description: 'Acceso directo a instituciones especializadas.'
        },
        {
            icon: 'lock',
            title: '100% Confidencial',
            description: 'Tus datos están protegidos y son anónimos.'
        }
    ];
}


