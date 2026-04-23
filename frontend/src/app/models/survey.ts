// Modelo para listar cuestionarios en la tabla
export interface Survey {
    id: number;
    title: string;
    description?: string;
    isActive: boolean;
    createdAt: Date;
    questions?: any[]; // Lista de preguntas — usar .length para el conteo
}

// DTO completo para crear/editar con todas las preguntas
export interface FullSurveyDTO {
    id?: number;
    title: string;
    description?: string;
    questions: QuestionDTO[];
}

// Estructura de cada pregunta (crear/editar)
export interface QuestionDTO {
    id: number;
    question: string;
    idZone: number;
    questionNumber: number;
}

// Pregunta con zona completa (para el quiz activo)
export interface QuestionWithZoneDTO {
    id: number;
    question: string;
    questionNumber: number;
    status: boolean;
    zone: {
        id: number;
        name: string;
        description: string;
        color: string;
        severity: number;
        status: boolean;
    };
}

// Survey activo con preguntas y zonas completas
export interface ActiveSurveyDTO {
    id: number;
    title: string;
    description?: string;
    questions: QuestionWithZoneDTO[];
}