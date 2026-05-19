package ec.edu.istr.violentometro.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GeneralReportDTO {

    private String surveyTitle;
    private String institution;
    private String period;
    private String generatedAt;

    // KPIs
    private Long totalParticipants;
    private Long totalCritical;
    private Integer totalInstitutions;

    // Vulnerabilidad por institución
    private List<VulnerabilityReportDTO> vulnerabilityTable;

    // Demográfico — estudiantes únicos por nivel de riesgo
    private List<DemographicReportDTO> genderDetail;
    private List<DemographicReportDTO> ethnicDetail;
    private List<DemographicReportDTO> disabilityDetail;
}