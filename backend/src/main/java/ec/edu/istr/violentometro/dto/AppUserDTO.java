package ec.edu.istr.violentometro.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
public class AppUserDTO {
    private Integer id;

    @NotNull("El DNI no puede ser nulo")
    private String dni;

    @NotNull("El ID del instituto es obligatorio")
    private Integer idInstitute;
}