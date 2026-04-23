package ec.edu.istr.violentometro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlertSignalDTO {
    private Integer questionId;
    private String  questionText;
    private String  zoneName;
}