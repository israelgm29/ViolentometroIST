package ec.edu.istr.violentometro.controller;

import ec.edu.istr.violentometro.dto.BulkUploadResult;
import ec.edu.istr.violentometro.service.BulkUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/app-users/bulk")
@RequiredArgsConstructor
public class BulkUploadController {

    private final BulkUploadService bulkUploadService;

    @PostMapping("/upload")
    public ResponseEntity<BulkUploadResult> upload(
            @RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(bulkUploadService.processFile(file));
    }
}