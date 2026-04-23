package ec.edu.istr.violentometro.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardResponseDTO {
    private List<ZoneStatisticsDTO> zones;
    private List<StatisticsDTO> ethnics;
    private List<StatisticsDTO> regions;
    private List<StatisticsDTO> disabilities;
    private List<VulnerabilityReportDTO> vulnerabilityTable;
    private List<StatisticsDTO> genders;      // Nueva métrica
    private Long totalVictims;                // Nueva métrica
    private Long criticalRiskCount;      // Cuántos usuarios únicos están en Zona Roja
    private String alertLevel;
    private List<StatisticsDTO> topQuestions; // Para el gráfico de barras horizontales
    private List<StatisticsDTO> alertsTrend;
}
