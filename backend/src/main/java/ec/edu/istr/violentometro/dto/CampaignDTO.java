package ec.edu.istr.violentometro.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class CampaignDTO {
    private Integer id;
    private String  title;
    private String  excerpt;
    private String  imageUrl;
    private String  externalUrl;
    private Boolean featured;
    private Boolean status;
    private LocalDate publishDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private OffsetDateTime createdAt;

    // Categoría aplanada
    private Integer categoryId;
    private String  categoryName;
    private String  categoryColor;
    private String  categoryIcon;
}