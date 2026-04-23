package ec.edu.istr.violentometro.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetDTO {

    @Valid
    @Size(min = 8)
    private String newPassword;
}
