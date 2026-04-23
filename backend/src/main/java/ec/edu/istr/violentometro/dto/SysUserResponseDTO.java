package ec.edu.istr.violentometro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysUserResponseDTO {
    private Integer id;
    private String firstname;
    private String secondname;
    private String firstLastname;
    private String secondLastname;
    private String dni;
    private String phone;
    private String address;
    private Boolean status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String email;


    // Usamos DTOs internos estáticos para no crear archivos extra
    private RoleInfo role;
    private InstituteInfo institute;

    @Data
    @AllArgsConstructor
    public static class RoleInfo {
        private Integer id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    public static class InstituteInfo {
        private Integer id;
        private String name;
    }
}