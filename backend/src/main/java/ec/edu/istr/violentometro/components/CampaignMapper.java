package ec.edu.istr.violentometro.components;

import ec.edu.istr.violentometro.dto.CampaignCategoryDTO;
import ec.edu.istr.violentometro.dto.CampaignDTO;
import ec.edu.istr.violentometro.model.Campaign;
import ec.edu.istr.violentometro.model.CampaignCategory;
import org.springframework.stereotype.Component;

@Component
public class CampaignMapper {

    public CampaignDTO toDto(Campaign c) {
        CampaignDTO dto = new CampaignDTO();
        dto.setId(c.getId());
        dto.setTitle(c.getTitle());
        dto.setExcerpt(c.getExcerpt());
        dto.setImageUrl(c.getImageUrl());
        dto.setExternalUrl(c.getExternalUrl());
        dto.setFeatured(c.getFeatured());
        dto.setStatus(c.getStatus());
        dto.setPublishDate(c.getPublishDate());
        dto.setStartDate(c.getStartDate());
        dto.setEndDate(c.getEndDate());
        dto.setCreatedAt(c.getCreatedAt());

        if (c.getCategory() != null) {
            dto.setCategoryId(c.getCategory().getId());
            dto.setCategoryName(c.getCategory().getName());
            dto.setCategoryColor(c.getCategory().getColor());
            dto.setCategoryIcon(c.getCategory().getIcon());
        }
        return dto;
    }

    public Campaign toEntity(CampaignDTO dto, CampaignCategory category) {
        Campaign c = new Campaign();
        c.setTitle(dto.getTitle());
        c.setExcerpt(dto.getExcerpt());
        c.setImageUrl(dto.getImageUrl());
        c.setExternalUrl(dto.getExternalUrl());
        c.setFeatured(dto.getFeatured() != null ? dto.getFeatured() : false);
        c.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
        c.setPublishDate(dto.getPublishDate());
        c.setStartDate(dto.getStartDate());
        c.setEndDate(dto.getEndDate());
        c.setCategory(category);
        return c;
    }

    public CampaignCategoryDTO toCategoryDto(CampaignCategory cat) {
        CampaignCategoryDTO dto = new CampaignCategoryDTO();
        dto.setId(cat.getId());
        dto.setName(cat.getName());
        dto.setColor(cat.getColor());
        dto.setIcon(cat.getIcon());
        dto.setStatus(cat.getStatus());
        return dto;
    }
}
