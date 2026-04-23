package ec.edu.istr.violentometro.dto;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class QuizResultDTO {
    private Integer idAppUser;
    private Integer idSurvey;
    private Integer totalScore;
    private String riskLevel;
    private Integer dominantZoneId;
    private OffsetDateTime createdAt;
}