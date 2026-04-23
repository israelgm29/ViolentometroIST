package ec.edu.istr.violentometro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private Integer id;
    private String  username;
    private String  email;
    private String  role;       // "ROLE_ADMIN" o "ROLE_ANALYST"
    private String  token;
}
