package ec.edu.istr.violentometro.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AppUserRequestDTO {
    private String dni;
    private Integer idGender;
    private LocalDate birthdate;
    private Integer idInstitute;
    private Integer idRegion;
    private Integer idDisability;
    private Integer idEthnicity;
}