package ec.edu.istr.violentometro.dto;

import lombok.Data;

@Data
public class PeriodComparisonRequestDTO {
    private String  startDate1;
    private String  endDate1;
    private String  startDate2;
    private String  endDate2;
    private Integer surveyId;
}