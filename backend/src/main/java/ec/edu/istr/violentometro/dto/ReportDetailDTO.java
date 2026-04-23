package ec.edu.istr.violentometro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDetailDTO {
    private String label;      // El nombre (Ej: "¿Te han golpeado?", "Femenino", "Mestizo")
    private String group;      // Una categoría extra (Ej: "Zona Física", "Discapacidad Motriz")
    private Long value;        // El conteo (cuántos "Sí")
    private Double percentage; // El porcentaje calculado
    private String color;     // Color asociado para visualización
}