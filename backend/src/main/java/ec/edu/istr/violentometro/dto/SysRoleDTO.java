package ec.edu.istr.violentometro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SysRoleDTO {
    private Integer id;

    @NotBlank(message = "El nombre del rol es obligatorio.")
    private String name;

    @NotBlank(message = "La descripción del rol es obligatoria.")
    private String description;
}