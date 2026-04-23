package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.ReportRequestDTO;
import ec.edu.istr.violentometro.service.CriticalCasesReportService;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final CriticalCasesReportService service;

    public ReportController(CriticalCasesReportService service) {
        this.service = service;
    }

    @PostMapping("/critical-cases/pdf")
    public ResponseEntity<byte[]> generatePdf(
            @RequestBody ReportRequestDTO request) {

        byte[] pdf = service.generatePdf(
                request.getStartDate(),
                request.getEndDate(),
                request.getSurveyId(),
                null // 👈 sin logo por ahora
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=casos-criticos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/critical-cases/excel")
    public ResponseEntity<byte[]> generateExcel(
            @RequestBody ReportRequestDTO request) {

        byte[] excel = service.generateExcel(
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
}