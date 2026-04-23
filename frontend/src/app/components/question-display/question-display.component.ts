import {Component, EventEmitter, Input, Output} from '@angular/core';
import {QuestionZone} from "../../models/question-zone";
import {NgClass} from "@angular/common";
import {MatIconModule} from "@angular/material/icon";

@Component({
    selector: 'app-question-display',
    standalone: true,
    imports: [
        MatIconModule,
        NgClass
    ],
    templateUrl: './question-display.component.html',
    styleUrl: './question-display.component.css',
})
export class QuestionDisplay {
// 1. INPUT: Recibe la pregunta a mostrar
    @Input() question!: QuestionZone;

    // 2. INPUT: Recibe la respuesta que ya dio el usuario (para pintar el botón seleccionado)
    @Input() currentAnswer: number | null = null; // 1 (sí) | 0 (no)

    // 3. INPUT: Recibe la dirección de la animación (opcional, para la presentación)
    @Input() slideDirection: 'left' | 'right' | 'none' = 'none';

    // 4. OUTPUT: Emite el evento cuando el usuario responde (true = Sí, false = No)
    @Output() answered = new EventEmitter<boolean>();

    onAnswerClick(answer: boolean): void {
        this.answered.emit(answer);
    }


}