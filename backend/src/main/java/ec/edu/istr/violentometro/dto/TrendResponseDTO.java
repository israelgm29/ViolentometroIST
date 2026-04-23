package ec.edu.istr.violentometro.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TrendResponseDTO {

    /** Participaciones totales por día */
    private List<StatisticsDTO> participationTrend;

    /** Casos críticos por día */
    private List<StatisticsDTO> criticalTrend;

    /** Puntaje promedio por día */
    private List<StatisticsDTO> avgScoreTrend;

    /** Niveles de riesgo por día — cada entry: { label: "critical", data: [0,2,1,...] } */
    private List<RiskLevelSeriesDTO> riskLevelSeries;

    /** KPIs rápidos del período */
    private Long totalParticipants;
    private Long totalCritical;
    private Double avgScore;

    @Data
    @Builder
    public static class RiskLevelSeriesDTO {
        private String       name;   // "critical", "high", "medium", "low"
        private List<Long>   data;   // valores por día en orden
        private List<String> dates;  // fechas correspondientes
    }
}