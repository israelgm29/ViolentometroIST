import {
    Component,
    EventEmitter,
    Input,
    OnChanges,
    Output,
    SimpleChanges,
    signal
} from '@angular/core';
import { QuestionZone } from '../../models/question-zone';
import { NgClass } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';

@Component({
    selector: 'app-question-display',
    standalone: true,
    imports: [MatIconModule, NgClass],
    templateUrl: './question-display.component.html',
    styleUrl: './question-display.component.scss',
})
export class QuestionDisplay implements OnChanges {

    @Input() question!: QuestionZone;
    @Input() currentAnswer: number | null = null;
    @Input() slideDirection: 'left' | 'right' | 'none' = 'none';
    @Output() answered = new EventEmitter<boolean>();

    // Pregunta que se renderiza actualmente (puede quedarse mientras anima la salida)
    visibleQuestion = signal<QuestionZone | null>(null);

    // Clase CSS activa en este momento: 'enter-left' | 'enter-right' | 'exit-left' | 'exit-right' | ''
    animClass = signal<string>('');

    private animating = false;

    ngOnChanges(changes: SimpleChanges): void {
        const qChange = changes['question'];

        if (!qChange) return;

        // Primera carga — sin animación
        if (qChange.firstChange) {
            this.visibleQuestion.set(qChange.currentValue);
            return;
        }

        // Si ya hay una animación en curso, ignoramos (no debería ocurrir)
        if (this.animating) return;

        this.animating = true;

        // 1. Animar SALIDA de la pregunta actual
        const exitClass = this.slideDirection === 'left' ? 'exit-left' : 'exit-right';
        this.animClass.set(exitClass);

        // 2. Tras 220ms (duración de salida) → cambiar pregunta + animar ENTRADA
        setTimeout(() => {
            this.visibleQuestion.set(qChange.currentValue);

            const enterClass = this.slideDirection === 'left' ? 'enter-right' : 'enter-left';
            this.animClass.set(enterClass);

            // 3. Tras 30ms (un frame) → quitar clase para que la transición CSS tome efecto
            requestAnimationFrame(() => {
                requestAnimationFrame(() => {
                    this.animClass.set('');
                    this.animating = false;
                });
            });
        }, 220);
    }

    onAnswerClick(answer: boolean): void {
        this.answered.emit(answer);
    }
}