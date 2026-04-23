package ec.edu.istr.violentometro.dto;

import lombok.Data;

@Data
public class CampaignCategoryDTO {
    private Integer id;
    private String  name;
    private String  color;
    private String  icon;
    private Boolean status;
}
