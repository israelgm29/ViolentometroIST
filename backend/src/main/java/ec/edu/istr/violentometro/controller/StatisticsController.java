package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.CriticalCasesReportDTO;
import ec.edu.istr.violentometro.dto.DashboardResponseDTO;
import ec.edu.istr.violentometro.dto.ReportDetailDTO;
import ec.edu.istr.violentometro.dto.TrendResponseDTO;
import ec.edu.istr.violentometro.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<DashboardResponseDTO> getDashboardData(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "surveyId", required = false) Integer surveyId) {

        OffsetDateTime start = OffsetDateTime.parse(startDate);
        OffsetDateTime end   = OffsetDateTime.parse(endDate);

        return ResponseEntity.ok(statisticsService.getFullDashboardData(start, end, surveyId));
    }

    @GetMapping("/detailed-report")
    public ResponseEntity<List<ReportDetailDTO>> getDetailedReport(
            @RequestParam String type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end,
            @RequestParam(value = "surveyId", required = false) Integer surveyId) {

        return ResponseEntity.ok(statisticsService.getDetailedReport(type, start, end, surveyId));
    }


    @GetMapping("/critical-cases")
    public ResponseEntity<CriticalCasesReportDTO> getCriticalCasesReport(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "surveyId", required = false) Integer surveyId) {

        OffsetDateTime start = OffsetDateTime.parse(startDate);
        OffsetDateTime end   = OffsetDateTime.parse(endDate);

        return ResponseEntity.ok(statisticsService.getCriticalCasesReport(start, end, surveyId));
    }

    @GetMapping("/trends")
    public ResponseEntity<TrendResponseDTO> getTrends(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end,
            @RequestParam Integer surveyId) {

        return ResponseEntity.ok(statisticsService.getTrends(start, end, surveyId));
    }

}