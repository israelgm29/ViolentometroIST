package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.dto.BulkUploadResult;
import ec.edu.istr.violentometro.model.*;
import ec.edu.istr.violentometro.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BulkUploadService {

    private final AppUserRepository appUserRepository;
    private final GenderRepository genderRepository;
    private final RegionRepository regionRepository;
    private final EthnicityRepository ethnicityRepository;
    private final InstituteRepository instituteRepository;
    private final DisabilityRepository disabilityRepository;

    public BulkUploadResult processFile(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename != null && filename.endsWith(".csv")) {
            return processCsv(file);
        }
        return processExcel(file);
    }

    // ── EXCEL ─────────────────────────────────────────────────
    private BulkUploadResult processExcel(MultipartFile file) throws Exception {
        BulkUploadResult result = new BulkUploadResult();
        List<BulkUploadResult.RowError> errors = new ArrayList<>();
        int created = 0, updated = 0;

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String dni = getCellValue(row.getCell(0));
            if (dni == null || dni.isBlank()) continue;

            try {
                boolean isNew = !appUserRepository.existsByDni(dni);
                saveOrUpdate(
                        dni,
                        getCellValue(row.getCell(1)), // genero
                        getCellValue(row.getCell(2)), // fecha_nacimiento
                        getCellValue(row.getCell(3)), // provincia
                        getCellValue(row.getCell(4)), // etnia
                        getCellValue(row.getCell(5)), // codigo_instituto
                        getCellValue(row.getCell(6))  // discapacidad
                );
                if (isNew) created++; else updated++;

            } catch (Exception e) {
                BulkUploadResult.RowError err = new BulkUploadResult.RowError();
                err.setRow(i + 1);
                err.setDni(dni);
                err.setReason(e.getMessage());
                errors.add(err);
            }
        }

        workbook.close();
        result.setTotalRows(sheet.getLastRowNum());
        result.setCreated(created);
        result.setUpdated(updated);
        result.setErrors(errors.size());
        result.setRowErrors(errors);
        return result;
    }

    // ── CSV ───────────────────────────────────────────────────
    private BulkUploadResult processCsv(MultipartFile file) throws Exception {
        BulkUploadResult result = new BulkUploadResult();
        List<BulkUploadResult.RowError> errors = new ArrayList<>();
        int created = 0, updated = 0;

        CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()));
        List<String[]> rows = reader.readAll();
        reader.close();

        for (int i = 1; i < rows.size(); i++) {
            String[] cols = rows.get(i);
            String dni = cols.length > 0 ? cols[0].trim() : null;
            if (dni == null || dni.isBlank()) continue;

            try {
                boolean isNew = !appUserRepository.existsByDni(dni);
                saveOrUpdate(
                        dni,
                        cols.length > 1 ? cols[1].trim() : null, // genero
                        cols.length > 2 ? cols[2].trim() : null, // fecha_nacimiento
                        cols.length > 3 ? cols[3].trim() : null, // provincia
                        cols.length > 4 ? cols[4].trim() : null, // etnia
                        cols.length > 5 ? cols[5].trim() : null, // codigo_instituto
                        cols.length > 6 ? cols[6].trim() : null  // discapacidad
                );
                if (isNew) created++; else updated++;

            } catch (Exception e) {
                BulkUploadResult.RowError err = new BulkUploadResult.RowError();
                err.setRow(i + 1);
                err.setDni(dni);
                err.setReason(e.getMessage());
                errors.add(err);
            }
        }

        result.setTotalRows(rows.size() - 1);
        result.setCreated(created);
        result.setUpdated(updated);
        result.setErrors(errors.size());
        result.setRowErrors(errors);
        return result;
    }

    // ── GUARDAR O ACTUALIZAR ──────────────────────────────────
    private void saveOrUpdate(String dni, String genero, String fechaNacimiento,
                              String provincia, String etnia,
                              String codigoInstituto, String discapacidad) {

        AppUser user = appUserRepository.findByDni(dni)
                .orElse(new AppUser());

        user.setDni(dni);

        user.setIdGender(genderRepository.findByNameIgnoreCase(genero)
                .orElseThrow(() -> new RuntimeException("Género no encontrado: " + genero)));

        user.setBirthdate(LocalDate.parse(fechaNacimiento,
                DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        user.setBirthdate(parseFecha(fechaNacimiento));

        user.setIdRegion(regionRepository.findByNameIgnoreCase(provincia)
                .orElseThrow(() -> new RuntimeException("Provincia no encontrada: " + provincia)));

        user.setIdEthnicity(ethnicityRepository.findByNameIgnoreCase(etnia)
                .orElseThrow(() -> new RuntimeException("Etnia no encontrada: " + etnia)));

        user.setIdInstitute(instituteRepository.findByCode(codigoInstituto)
                .orElseThrow(() -> new RuntimeException("Instituto no encontrado con código: " + codigoInstituto)));

        // Discapacidad es opcional
        if (discapacidad != null && !discapacidad.isBlank()
                && !discapacidad.equalsIgnoreCase("Ninguna")
                && !discapacidad.equalsIgnoreCase("N/A")) {
            user.setIdDisability(disabilityRepository.findByNameIgnoreCase(discapacidad)
                    .orElse(null));
        } else {
            user.setIdDisability(null);
        }

        appUserRepository.save(user);
    }

    // ── LEER CELDA EXCEL ─────────────────────────────────────
    private String getCellValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue()
                            .toLocalDate()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
                // Número entero
                yield String.valueOf((long) cell.getNumericCellValue());
            }
            case FORMULA -> {
                // Fórmulas que resultan en fecha
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue()
                            .toLocalDate()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
                yield cell.getStringCellValue().trim();
            }
            default -> null;
        };
    }

    private LocalDate parseFecha(String fecha) {
        List<DateTimeFormatter> formatos = List.of(
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("d/M/yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy")
        );

        for (DateTimeFormatter formato : formatos) {
            try {
                return LocalDate.parse(fecha, formato);
            } catch (Exception ignored) {}
        }
        throw new RuntimeException("Formato de fecha inválido: " + fecha);
    }
}