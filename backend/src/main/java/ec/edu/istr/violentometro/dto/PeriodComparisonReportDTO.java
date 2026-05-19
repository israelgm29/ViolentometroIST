package ec.edu.istr.violentometro.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO del reporte comparativo entre dos períodos.
 */
@Data
@Builder
public class PeriodComparisonReportDTO {

    private String surveyTitle;
    private String generatedAt;

    // Etiquetas de período
    private String period1Label;
    private String period2Label;

    // Participación
    private Long   period1Participants;
    private Long   period2Participants;
    private Double participationChange;   // % de variación

    // Casos críticos
    private Long   period1Critical;
    private Long   period2Critical;
    private Double criticalChange;

    // Puntaje promedio
    private Double period1AvgScore;
    private Double period2AvgScore;
    private Double avgScoreChange;

    // Tasa de criticidad
    private Double period1CriticalRate;
    private Double period2CriticalRate;
    private Double criticalRateChange;

    // Niveles de riesgo período 1
    private Long   period1LevelCritical;
    private Long   period1LevelHigh;
    private Long   period1LevelMedium;
    private Long   period1LevelLow;

    // Niveles de riesgo período 2
    private Long   period2LevelCritical;
    private Long   period2LevelHigh;
    private Long   period2LevelMedium;
    private Long   period2LevelLow;

    // Conclusión general
    private String conclusion; // "MEJORA", "DETERIORO", "ESTABLE"
}