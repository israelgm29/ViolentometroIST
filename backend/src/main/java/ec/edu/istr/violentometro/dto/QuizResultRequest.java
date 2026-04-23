package ec.edu.istr.violentometro.dto;

import lombok.Data;

@Data
public class QuizResultRequest {
    private Integer idAppUser;
    private Integer idSurvey;
    private Integer totalScore;
    private String riskLevel;
    private Integer dominantZoneId;
}