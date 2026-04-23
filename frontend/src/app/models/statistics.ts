export interface StatisticsDTO {
    label: string;
    count: number;
}

export interface ZoneStatisticsDTO {
    zoneName: string;
    color: string;
    totalAnswers: number;
    percentage: number;
}

export interface VulnerabilityReportDTO {
    institutionName: string;
    highRiskCount: number;
    moderateRiskCount: number;
    totalVictims: number;
}

export interface ReportDetailDTO{
    label: string;      // La pregunta o categoría
    group: string;      // La zona o grupo superior
    value: number;      // El conteo de 'Sí'
    percentage: number; // El porcentaje calculado
    color: string;
}

export interface DashboardData {
    criticalRiskCount: number;
    alertLevel: string;
    zones: ZoneStatisticsDTO[];
    ethnics: StatisticsDTO[];
    regions: StatisticsDTO[];
    disabilities: StatisticsDTO[];
    genders: StatisticsDTO[];
    vulnerabilityTable: VulnerabilityReportDTO[];
    totalVictims: number;
    topQuestions: StatisticsDTO[];
    alertsTrend: StatisticsDTO[];
}


export interface RiskLevelSeriesDTO {
    name:  string;   // 'critical', 'high', 'medium', 'low'
    data:  number[];
    dates: string[];
}

export interface TrendResponseDTO {
    participationTrend: StatisticsDTO[];
    criticalTrend:      StatisticsDTO[];
    avgScoreTrend:      StatisticsDTO[];
    riskLevelSeries:    RiskLevelSeriesDTO[];
    totalParticipants:  number;
    totalCritical:      number;
    avgScore:           number;
}