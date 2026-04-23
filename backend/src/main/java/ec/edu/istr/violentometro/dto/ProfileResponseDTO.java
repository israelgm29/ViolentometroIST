package ec.edu.istr.violentometro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileResponseDTO {
    private Integer id;
    private String  firstname;
    private String  secondname;
    private String  firstLastname;
    private String  secondLastname;
    private String  email;
    private String  phone;
    private String  address;
    private String  dni;
    private String  role;
    private Boolean status;
}
