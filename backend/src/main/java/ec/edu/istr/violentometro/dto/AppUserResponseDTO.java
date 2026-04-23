package ec.edu.istr.violentometro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserResponseDTO {
    private Integer id;
    private String dni;
    private LocalDate birthdate;
    private Boolean status;

    // Objetos anidados para información detallada
    private GenderInfo gender;
    private InstituteInfo institute;
    private RegionInfo region;
    private DisabilityInfo disability;
    private EthnicityInfo ethnicity;

    @Data
    @AllArgsConstructor
    public static class GenderInfo {
        private Integer id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    public static class InstituteInfo {
        private Integer id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    public static class RegionInfo {
        private Integer id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    public static class DisabilityInfo {
        private Integer id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    public static class EthnicityInfo {
        private Integer id;
        private String name;
    }
}