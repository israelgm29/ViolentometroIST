package ec.edu.istr.violentometro.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
public class QuestionDTO {
    private Integer id;

    @NotNull("La pregunta es obligatoria")
    private String question;

    @NotNull("El número de pregunta es obligatorio")
    private Integer questionNumber;

    @NotNull("La zona es obligatoria")
    private Integer idZone;

    private Boolean status;
}
