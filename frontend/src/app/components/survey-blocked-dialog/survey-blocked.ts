import { Component, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { animate, style, transition, trigger } from '@angular/animations';

/**
 * Pantalla amigable que se muestra cuando el estudiante
 * ya respondió el cuestionario hoy.
 */
@Component({
    selector: 'app-survey-blocked',
    standalone: true,
    imports: [MatCardModule, MatButtonModule, MatIconModule, RouterLink],
    animations: [
        trigger('fadeIn', [
            transition(':enter', [
                style({ opacity: 0, transform: 'translateY(16px)' }),
                animate('400ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
            ])
        ])
    ],
    template: `
        <div class="blocked-container" @fadeIn>
            <mat-card class="blocked-card">

                <div class="icon-wrap">
                    <mat-icon>event_available</mat-icon>
                </div>

                <h1>¡Ya respondiste hoy!</h1>

                <p class="message">{{ message() }}</p>

                <div class="info-box">
                    <mat-icon>info_outline</mat-icon>
                    <span>
                        El cuestionario está diseñado para completarse
                        <strong>una vez por día</strong>. Vuelve mañana
                        para registrar tu respuesta.
                    </span>
                </div>

                <button mat-flat-button color="primary" routerLink="/home">
                    <mat-icon>home</mat-icon>
                    Volver al inicio
                </button>

            </mat-card>
        </div>
    `,
    styles: [`
        .blocked-container {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #f1f5f9;
            padding: 24px;
        }

        .blocked-card {
            max-width: 480px;
            width: 100%;
            padding: 40px 32px;
            text-align: center;
            border-radius: 16px !important;
            box-shadow: 0 4px 24px rgba(0,0,0,0.08) !important;
        }

        .icon-wrap {
            width: 72px;
            height: 72px;
            border-radius: 50%;
            background: #ede9fe;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 20px;
        }

        .icon-wrap mat-icon {
            font-size: 36px;
            width: 36px;
            height: 36px;
            color: #6d28d9;
        }

        h1 {
            font-size: 22px;
            font-weight: 700;
            color: #0f172a;
            margin: 0 0 10px;
        }

        .message {
            font-size: 14px;
            color: #64748b;
            margin: 0 0 24px;
            line-height: 1.6;
        }

        .info-box {
            display: flex;
            align-items: flex-start;
            gap: 10px;
            background: #f8fafc;
            border: 1px solid #e2e8f0;
            border-radius: 10px;
            padding: 14px 16px;
            text-align: left;
            font-size: 13px;
            color: #475569;
            margin-bottom: 28px;
            line-height: 1.5;
        }

        .info-box mat-icon {
            font-size: 18px;
            width: 18px;
            height: 18px;
            color: #6d28d9;
            flex-shrink: 0;
            margin-top: 1px;
        }

        button {
            width: 100%;
            height: 44px;
            font-size: 14px;
        }
    `]
})
export class SurveyBlockedComponent {
    public message = signal('Ya respondiste el cuestionario hoy. Vuelve mañana.');

    constructor(private route: ActivatedRoute) {
        const msg = this.route.snapshot.queryParamMap.get('message');
        if (msg) this.message.set(msg);
    }
}