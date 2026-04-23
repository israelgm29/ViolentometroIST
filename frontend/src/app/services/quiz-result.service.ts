import {inject, Injectable, signal} from '@angular/core';
import {QuizResult, QuizResultRequest} from "../models/quiz-result";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";

export interface ResultData {
    nivel: 'bajo' | 'medio' | 'alto' | 'critico';
    titulo: string;
    mensaje: string;
    icon: string;
    recomendaciones: string[];
    colorClass: string;
    color?: string;
    image?: string;
}

@Injectable({
    providedIn: 'root'
})
export class QuizResultService {
    private http = inject(HttpClient);
    private apiUrl = `${environment.apiUrl}/v1/quiz-results`;
    private currentResult = signal<QuizResult | null>(null);

    // Método para guardar en el SERVIDOR
    saveToBackend(data: QuizResultRequest): Observable<any> {
        return this.http.post(this.apiUrl, data);
    }

    saveResult(result: QuizResult): void {
        this.currentResult.set(result);
    }

    getResultData(): ResultData {
        const result = this.currentResult();
        if (!result) return this.getDefaultResult();
        return this.mapResultToData(result);
    }

    clearResult(): void {
        this.currentResult.set(null);
    }

    private mapResultToData(result: QuizResult): ResultData {
        // PRIORIDAD TOTAL: Si el resultado trae zona del backend, usamos sus textos
        if (result.zone) {
            return {
                nivel: this.severityToNivel(result.zone.severity),
                titulo: result.zone.resultTitle || 'Resultado del Análisis',
                mensaje: result.zone.resultMessage || 'Basado en tus respuestas, este es tu nivel de riesgo.',
                icon: this.severityToIcon(result.zone.severity),
                recomendaciones: result.zone.recommendations || [],
                colorClass: this.severityToColorClass(result.zone.severity),
                image: this.defaultImage(result.riskLevel)
            };
        }

        // Si por alguna razón no hay zona (error de carga), usamos un fallback mínimo
        return this.fallbackByRiskLevel(result.riskLevel);
    }

    // ─── Mapeos Visuales Basados en Severidad ─────────────────────────────────

    private severityToNivel(s: number): 'bajo' | 'medio' | 'alto' | 'critico' {
        if (s <= 1) return 'bajo';
        if (s === 2) return 'medio';
        if (s === 3) return 'alto';
        return 'critico';
    }

    private severityToIcon(s: number): string {
        if (s <= 1) return 'info';
        if (s === 2) return 'warning';
        if (s === 3) return 'error';
        return 'report';
    }

    private severityToColorClass(s: number): string {
        if (s <= 1) return 'nivel-bajo';
        if (s === 2) return 'nivel-medio';
        if (s === 3) return 'nivel-alto';
        return 'nivel-critico';
    }

    private defaultImage(riskLevel: string): string {
        const images: Record<string, string> = {
            neutral: 'assets/images/neutral.png',
            low: 'assets/images/neutral.png',
            medium: 'assets/images/neutral.png',
            high: 'assets/images/Gender violence women.svg',
            critical: 'assets/images/neutral.png'
        };
        return images[riskLevel] || 'assets/images/neutral.png';
    }

    // ─── Fallbacks de Seguridad ───────────────────────────────────────────────

    private fallbackByRiskLevel(riskLevel: string): ResultData {
        const s = this.riskLevelToSeverity(riskLevel);
        return {
            nivel: this.severityToNivel(s),
            titulo: 'Resultado de Evaluación',
            mensaje: 'Se ha detectado un nivel de riesgo que requiere tu atención.',
            icon: this.severityToIcon(s),
            recomendaciones: ['Por favor, acércate a las oficinas de bienestar para más información.'],
            colorClass: this.severityToColorClass(s),
            image: this.defaultImage(riskLevel)
        };
    }

    private riskLevelToSeverity(riskLevel: string): number {
        const map: Record<string, number> = {
            neutral: 0, low: 1, medium: 2, high: 3, critical: 4
        };
        return map[riskLevel] ?? 0;
    }

    private getDefaultResult(): ResultData {
        return {
            nivel: 'bajo',
            titulo: 'Sin Resultados',
            mensaje: 'No se encontraron resultados recientes.',
            icon: 'help_outline',
            recomendaciones: ['Completa el cuestionario para obtener un diagnóstico.'],
            colorClass: 'nivel-bajo',
            image: 'assets/images/neutral.png'
        };
    }
}