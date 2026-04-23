package ec.edu.istr.violentometro.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SysUserDTO {
    private Integer id;

    @NotBlank
    private String firstname;
    private String secondname;

    @NotBlank
    private String firstLastname;
    private String secondLastname;

    @NotNull
    @Size(min = 10, max = 10, message = "El DNI debe tener 10 caracteres.")
    private String dni;

    private String phone;
    private String address;

    // Solo se envía el ID de la relación
    @NotNull
    private Integer idRole;

    @NotNull
    private Integer idInstitute;

    private Boolean status;

    private  String email;

}
