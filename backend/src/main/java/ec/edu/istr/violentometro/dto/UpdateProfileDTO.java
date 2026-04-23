package ec.edu.istr.violentometro.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileDTO {
    @NotBlank(message = "El nombre es requerido")
    private String firstname;

    private String secondname;

    @NotBlank(message = "El apellido es requerido")
    private String firstLastname;

    private String secondLastname;

    @Email(message = "Email inválido")
    @NotBlank(message = "El email es requerido")
    private String email;

    private String phone;
    private String address;
}
