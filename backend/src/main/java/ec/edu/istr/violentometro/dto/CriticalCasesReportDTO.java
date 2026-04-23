package ec.edu.istr.violentometro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CriticalCasesReportDTO {
    private Integer        surveyId;
    private String         surveyTitle;
    private OffsetDateTime generatedAt;
    private Integer        totalCritical;
    private List<CriticalCaseDTO> cases;
}