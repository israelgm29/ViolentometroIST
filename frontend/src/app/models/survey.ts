export interface Survey {
    id:          number;
    title:       string;
    description?: string;
    isActive:    boolean;
    createdAt:   string;
    idInstituto: number;
    questions?:  any[];  // opcional — usar .length para conteo en la tabla
}

export interface FullSurveyDTO {
    id?:          number;
    title:        string;
    description:  string;
    isActive?:    boolean;
    createdAt?:   string;
    idInstituto?: number;
    questions:    QuestionDTO[];
}

export interface QuestionDTO {
    id:             number;
    question:       string;
    idZone:         number;
    questionNumber: number;
}

export interface ActiveSurveyDTO {
    id:          number;
    title:       string;
    description: string;
    questions:   ActiveQuestionDTO[];
}

export interface ActiveQuestionDTO {
    id:             number;
    question:       string;
    questionNumber: number;
    status:         boolean;
    zone:           ZoneDTO;
}

export interface ZoneDTO {
    id:              number;
    name:            string;
    description:     string;
    color:           string;
    severity:        number;
    status:          boolean;
    resultTitle:     string;
    resultMessage:   string;
    recommendations: string[];
}