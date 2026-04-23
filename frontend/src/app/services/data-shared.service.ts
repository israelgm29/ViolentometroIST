import { Injectable, signal, computed } from '@angular/core';
import { Survey } from '../models/survey';

@Injectable({
    providedIn: 'root'
})
export class DataSharedService {
    // Fechas
    readonly startDate = signal<Date>(new Date(new Date().setMonth(new Date().getMonth() - 1)));
    readonly endDate = signal<Date>(new Date());

    // Survey seleccionado
    readonly selectedSurvey = signal<Survey | null>(null);

    // Computed strings ISO para la API
    readonly startISO = computed(() => this.startDate().toISOString());
    readonly endISO = computed(() => this.endDate().toISOString());

    updateDates(start: Date, end: Date) {
        this.startDate.set(start);
        this.endDate.set(end);
    }

    updateSurvey(survey: Survey) {
        this.selectedSurvey.set(survey);
    }

    clearSurvey() {
        this.selectedSurvey.set(null);
    }
}