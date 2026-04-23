package ec.edu.istr.violentometro.dto;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class SurveyDTO {
    private Integer id;
    private String title;
    private String description;
    private Boolean isActive;
    private OffsetDateTime createdAt;
    private List<QuestionDTO> questions;

    @Data
    public static class QuestionDTO {
        private Integer id;
        private String question;
        private Integer idZone;
        private Integer questionNumber;
    }
}