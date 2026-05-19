package ec.edu.istr.violentometro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para reportes demográficos basados en quiz_result.
 * Cada fila representa: categoría + nivel de riesgo + cantidad de estudiantes únicos.
 *
 * Ejemplos:
 *   label="Mestizo"   group="critical" value=2
 *   label="Mestizo"   group="high"     value=1
 *   label="Femenino"  group="critical" value=3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemographicReportDTO {
    private String label;      // Nombre de la categoría (género, etnia, discapacidad)
    private String group;      // Nivel de riesgo: critical, high, medium, low
    private Long   value;      // Cantidad de estudiantes únicos
    private Double percentage; // Se calcula en el service
}