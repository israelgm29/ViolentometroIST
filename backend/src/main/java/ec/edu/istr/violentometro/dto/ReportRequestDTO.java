package ec.edu.istr.violentometro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDTO {

    private String startDate;
    private String endDate;
    private Integer surveyId;
}
