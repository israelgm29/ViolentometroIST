package ec.edu.istr.violentometro.dto;

import lombok.Data;
import java.util.List;

@Data
public class BulkUploadResult {
    private int totalRows;
    private int created;
    private int updated;
    private int errors;
    private List<RowError> rowErrors;

    @Data
    public static class RowError {
        private int row;
        private String dni;
        private String reason;
    }
}