package ec.edu.istr.violentometro.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAnswerDTO {
    private Integer idAnswer;

    @NotNull(message = "El valor de la respuesta (true/false) es obligatorio.")
    private Boolean answer;

    @NotNull(message = "El ID del usuario es obligatorio.")
    private Integer idAppUser;

    @NotNull(message = "El ID de la pregunta es obligatorio.")
    private Integer idQuestion;
}