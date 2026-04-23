export type RiskLevel = 'neutral' | 'low' | 'medium' | 'high' | 'critical';

export interface QuizZone {
    id: number;
    name: string;
    color: string;
    severity: number;
    resultTitle?: string;
    resultMessage?: string;
    recommendations?: string[];
}

export interface QuizResult {
    userId: number;
    totalQuestions: number;
    answeredQuestions: number;
    yesCount: number;
    noCount: number;
    totalPoints: number;
    riskLevel: RiskLevel;
    timestamp: Date;
    zone?: QuizZone; // Zona dominante del resultado — datos dinámicos del admin
}

export interface QuizResultRequest {
    idAppUser: number;
    idSurvey: number;
    totalScore: number;
    riskLevel: string;
    dominantZoneId?: number;
}