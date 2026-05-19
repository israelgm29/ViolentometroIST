package ec.edu.istr.violentometro.dto;

import lombok.Data;

@Data
public class StudentTrackingRequestDTO {
    private String  dni;
    private String  startDate;
    private String  endDate;
    private Integer surveyId;
}