export interface AlertSignal {
    questionId:   number;
    questionText: string;
    zoneName:     string;
}

export interface CriticalCase {
    responseId?:   number;
    userId:        number;
    victimDni?:    string;   // DNI de la víctima
    age?:          number;
    gender?:       string;
    ethnicity?:    string,
    institution?:  string;
    zoneName:      string;
    registeredAt:  string;
    riskScore:     number;
    riskLevel:     'CRÍTICO' | 'ALTO' | 'MODERADO';
    alertSignals:  AlertSignal[];
}

export interface CriticalCasesReport {
    surveyId:      number;
    surveyTitle:   string;
    generatedAt:   string;
    totalCritical: number;
    cases:         CriticalCase[];
}