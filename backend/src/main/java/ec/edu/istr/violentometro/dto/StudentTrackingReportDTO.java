package ec.edu.istr.violentometro.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DTO del reporte de seguimiento por estudiante.
 */
@Data
@Builder
public class StudentTrackingReportDTO {

    // Datos del estudiante
    private String dni;
    private String gender;
    private String ethnicity;
    private String disability;
    private String region;
    private String institute;
    private Integer ageApprox;

    // Resumen del período
    private Integer totalSessions;
    private String firstSession;
    private String lastSession;
    private String currentRiskLevel;  // nivel de la sesión más reciente
    private Double avgScore;
    private String trend;             // "MEJORANDO", "EMPEORANDO", "ESTABLE"

    // Historial de sesiones
    private List<SessionDTO> sessions;

    @Data
    @Builder
    public static class SessionDTO {
        private Integer sessionNumber;
        private String date;
        private String riskLevel;
        private Integer score;
        private String dominantZone;
        private String trend;   // vs sesión anterior
    }
}