package ec.edu.istr.violentometro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StatisticsDTO {
    private String label; // Nombre de la categoría, provincia o discapacidad
    private Long count;   // Cantidad de respuestas positivas (true)
}