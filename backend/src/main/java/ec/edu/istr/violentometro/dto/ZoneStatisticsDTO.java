package ec.edu.istr.violentometro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ZoneStatisticsDTO {
    private String zoneName;
    private String color;
    private Long totalAnswers;
    private Double percentage;
}
