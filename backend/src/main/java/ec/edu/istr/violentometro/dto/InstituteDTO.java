package ec.edu.istr.violentometro.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InstituteDTO {
    private Integer id;

    @NotBlank(message = "El código del instituto es obligatorio.")
    private String code;

    @NotBlank(message = "El nombre del instituto es obligatorio.")
    private String name;

    private String shortName;

    @NotBlank(message = "La dirección del instituto es obligatoria.")
    private String address;

    @NotBlank(message = "La ciudad donde se ubica el instituto es obligatoria.")
    private String city;

    @NotBlank(message = "La provincia donde se ubica el instituto es obligatoria.")
    private String province;

    @NotBlank(message = "El país es obligatorio.")
    private String country;

    @NotBlank(message = "El teléfono del instituto es obligatorio.")
    private String phone;

    @NotBlank(message = "El email del instituto es obligatoria.")
    @Email(message = "El correo electrónico no es válido.")
    private String email;

    private String webUrl;

    private Boolean status;

    private OffsetDateTime createdDate;
}