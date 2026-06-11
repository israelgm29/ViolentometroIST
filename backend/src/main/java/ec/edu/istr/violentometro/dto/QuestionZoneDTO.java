package ec.edu.istr.violentometro.dto;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionZoneDTO {
    private Integer id;

    @NotNull(message = "La pregunta es obligatoria")
    private String question;

    @NotNull(message = "El número de pregunta es obligatorio")
    private Integer questionNumber;

    @NotNull(message = "La zona es obligatoria")
    private ViolenceZoneDTO zone;

    private Boolean status;
}
