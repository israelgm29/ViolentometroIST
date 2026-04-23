import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { FooterComponent } from '../../../shared/footer/footer.component';

@Component({
    selector: 'app-help',
    standalone: true,
    imports: [MatIconModule, FooterComponent],
    templateUrl: './help.component.html',
    styleUrls: ['./help.component.scss']
})
export class HelpComponent {

    contacts = [
        {
            name: 'ECU 911',
            icon: 'verified_user',
            class: 'ecu',
            phone: '911',
            description: 'Emergencias 24/7'
        },
        {
            name: 'POLICÍA NACIONAL',
            icon: 'local_police',
            class: 'police',
            phone: '101',
            description: 'Denuncia y protección'
        },
        {
            name: 'FUNDACIÓN ALIADA',
            icon: 'school',
            class: 'mineduc',
            phone: '+593 983822831',
            description: 'Prevención de violencia de género en la comunidad educativa'
        },
        {
            name: 'MINISTERIO DE SALUD PÚBLICA',
            icon: 'medical_services',
            class: 'health',
            phone: '171',
            description: 'Atención psicológica gratuita'
        }
    ];

    warningSigns = [
        {
            icon: 'visibility',
            title: 'Control excesivo',
            text: 'Monitoreo constante de tus actividades, llamadas, mensajes y ubicación.'
        },
        {
            icon: 'group_off',
            title: 'Aislamiento social',
            text: 'Te aleja de amigos, familia y personas que te apoyan.'
        },
        {
            icon: 'favorite_border',
            title: 'Celos extremos',
            text: 'Posesividad excesiva y acusaciones infundadas de infidelidad.'
        },
        {
            icon: 'sentiment_very_dissatisfied',
            title: 'Humillación constante',
            text: 'Críticas destructivas, insultos y menosprecio de tu valor.'
        },
        {
            icon: 'warning',
            title: 'Amenazas e intimidación',
            text: 'Uso de amenazas verbales o gestuales para controlarte o asustarte.'
        },
        {
            icon: 'personal_injury',
            title: 'Violencia física',
            text: 'Cualquier tipo de agresión física, sin importar la intensidad.'
        }
    ];
}