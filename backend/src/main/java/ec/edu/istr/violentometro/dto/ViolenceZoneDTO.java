package ec.edu.istr.violentometro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViolenceZoneDTO {
    private Integer id;

    @NotBlank(message = "El nombre de la zona es obligatorio.")
    private String name;

    @NotBlank(message = "La descripción es obligatoria.")
    private String description;

    @NotBlank(message = "El color (código) es obligatorio.")
    private String color;

    @NotNull(message = "La severidad (nivel) es obligatoria.")
    private Integer severity;

    private Boolean status;

    private String resultTitle;

    private String resultMessage;

    private List<String> recommendations;
}