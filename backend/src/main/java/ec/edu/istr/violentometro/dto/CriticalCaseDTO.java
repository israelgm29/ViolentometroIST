package ec.edu.istr.violentometro.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CriticalCaseDTO {

    private Integer         userId;
    private String          victimDni;
    private String          gender;
    private String          ethnicity;
    private String          institution;
    private Integer         age;
    private OffsetDateTime  registeredAt;
    private Integer         riskScore;
    private String          riskLevel;
    private List<AlertSignalDTO> alertSignals;

    public CriticalCaseDTO(Integer userId, String victimDni,
                           String gender, String ethnicity, String institution,
                           OffsetDateTime registeredAt, Long alertCount) {
        this.userId       = userId;
        this.victimDni    = victimDni;
        this.gender       = gender;
        this.ethnicity    = ethnicity;
        this.institution  = institution;
        this.registeredAt = registeredAt;
        this.riskScore    = alertCount.intValue();
        this.riskLevel    = resolveRiskLevel(alertCount.intValue());
    }

    private static String resolveRiskLevel(int score) {
        if (score >= 5) return "CRÍTICO";
        if (score >= 3) return "ALTO";
        return "MODERADO";
    }

    private Integer calculateAge(LocalDate birthDate, OffsetDateTime registeredAt) {
        if (birthDate == null || registeredAt == null) return null;

        return Period.between(
                birthDate,
                registeredAt.toLocalDate()
        ).getYears();
    }
}