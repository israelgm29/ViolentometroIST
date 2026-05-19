package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.ParticipationReportDTO;
import ec.edu.istr.violentometro.dto.PeriodComparisonRequestDTO;
import ec.edu.istr.violentometro.dto.ReportRequestDTO;
import ec.edu.istr.violentometro.dto.StudentTrackingRequestDTO;
import ec.edu.istr.violentometro.service.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final CriticalCasesReportService criticalService;
    private final GeneralReportService generalService;
    private final StudentTrackingReportService trackingService;
    private final PeriodComparisonReportService comparisonService;
    private final ParticipationReportService participationService;

    public ReportController(CriticalCasesReportService criticalService,
                            GeneralReportService generalService, StudentTrackingReportService trackingService, PeriodComparisonReportService comparisonService, ParticipationReportService participationService) {
        this.criticalService = criticalService;
        this.generalService = generalService;
        this.trackingService = trackingService;
        this.comparisonService = comparisonService;
        this.participationService = participationService;
    }

    // ── Casos Críticos ───────────────────────────────────────────────

    @PostMapping("/critical-cases/pdf")
    public ResponseEntity<byte[]> generateCriticalPdf(
            @RequestBody ReportRequestDTO request) {

        byte[] pdf = criticalService.generatePdf(
                request.getStartDate(),
                request.getEndDate(),
                request.getSurveyId(),
                null
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=casos-criticos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/critical-cases/excel")
    public ResponseEntity<byte[]> generateCriticalExcel(
            @RequestBody ReportRequestDTO request) {

        byte[] excel = criticalService.generateExcel(
                request.getStartDate(),
                request.getEndDate(),
                request.getSurveyId()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=casos-criticos.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    // ── Reporte General ──────────────────────────────────────────────

    /**
     * POST /api/v1/reports/general/pdf
     * Genera el reporte general institucional con:
     * - KPIs del período
     * - Vulnerabilidad por institución
     * - Análisis por género, etnia y discapacidad
     */
    @PostMapping("/general/pdf")
    public ResponseEntity<byte[]> generateGeneralPdf(
            @RequestBody ReportRequestDTO request) {

        byte[] pdf = generalService.generatePdf(
                request.getStartDate(),
                request.getEndDate(),
                request.getSurveyId(),
                null // logo: null por ahora
        );

        String filename = "reporte-general-" + request.getStartDate().substring(0, 10) + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/student-tracking/excel")
    public ResponseEntity<byte[]> generateStudentTrackingExcel(
            @RequestBody StudentTrackingRequestDTO request) {

        byte[] excel = trackingService.generateExcel(
                request.getDni(),
                request.getStartDate(),
                request.getEndDate(),
                request.getSurveyId()
        );

        String filename = "seguimiento-" + request.getDni() + "-"
                + java.time.LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    /**
     * POST /api/v1/reports/period-comparison/excel
     */
    @PostMapping("/period-comparison/excel")
    public ResponseEntity<byte[]> generatePeriodComparisonExcel(
            @RequestBody PeriodComparisonRequestDTO request) {

        byte[] excel = comparisonService.generateExcel(
                request.getStartDate1(), request.getEndDate1(),
                request.getStartDate2(), request.getEndDate2(),
                request.getSurveyId()
        );

        String filename = "comparativo-periodos-" + java.time.LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    /**
     * POST /api/v1/reports/participation/excel
     */

    @PostMapping("/participation/excel")
    public ResponseEntity<byte[]> generateParticipationExcel(
            @RequestBody ReportRequestDTO request) {

        byte[] excel = participationService.generateExcel(
                request.getStartDate(),
                request.getEndDate(),
                request.getSurveyId()
        );

        String filename = "participacion-" + java.time.LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }


}