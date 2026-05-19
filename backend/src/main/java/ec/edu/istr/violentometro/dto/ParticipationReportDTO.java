package ec.edu.istr.violentometro.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ParticipationReportDTO {

    private String surveyTitle;
    private String period;
    private String generatedAt;

    // KPIs
    private Long   totalSessions;
    private Long   totalParticipants;
    private Long   totalCritical;
    private Double avgSessionsPerDay;

    // Hoja 1: Resumen diario
    private List<DailySummaryDTO> dailySummary;

    // Hoja 2: Detalle individual
    private List<SessionDetailDTO> sessionDetails;

    @Data
    @Builder
    public static class DailySummaryDTO {
        private String date;
        private Long   totalSessions;
        private Long   critical;
        private Long   high;
        private Long   medium;
        private Long   low;
        private Double avgScore;
    }

    @Data
    @Builder
    public static class SessionDetailDTO {
        private String  date;
        private String  time;
        private String  dni;
        private String  gender;
        private String  ethnicity;
        private String  institute;
        private String  riskLevel;
        private Integer score;
        private String  dominantZone;
    }
}