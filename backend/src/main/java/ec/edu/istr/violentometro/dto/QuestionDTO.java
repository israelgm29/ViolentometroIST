package ec.edu.istr.violentometro.dto;

import lombok.Data;

@Data
public class QuestionDTO {
    private Integer id;
    private String question;
    private Integer questionNumber;
    private Integer idZone;
    private Boolean status;
}
