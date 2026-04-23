package ec.edu.istr.violentometro.dto;

import lombok.Data;
import java.util.List;

@Data
public class ActiveSurveyDTO {
    private Integer id;
    private String title;
    private String description;
    private List<QuestionDTO> questions;

    @Data
    public static class QuestionDTO {
        private Integer id;
        private String question;
        private Integer questionNumber;
        private Boolean status;
        private ZoneDTO zone;
    }

    @Data
    public static class ZoneDTO {
        private Integer id;
        private String name;
        private String description;
        private String color;
        private Integer severity;
        private Boolean status;
        private String resultTitle;
        private String resultMessage;
        private List<String> recommendations; // Mantenemos la lista aquí
    }
}