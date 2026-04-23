package ec.edu.istr.violentometro.components;

import ec.edu.istr.violentometro.dto.MasterCatalogDTO;
import ec.edu.istr.violentometro.model.Gender;
import ec.edu.istr.violentometro.model.Region;
import ec.edu.istr.violentometro.model.Disability;
import ec.edu.istr.violentometro.model.Ethnicity;
import org.springframework.stereotype.Component;

@Component
public class CatalogMapper {

    public MasterCatalogDTO toDto(Region entity) {
        return (entity == null) ? null : new MasterCatalogDTO(entity.getId(), entity.getName());
    }

    public MasterCatalogDTO toDto(Disability entity) {
        return (entity == null) ? null : new MasterCatalogDTO(entity.getId(), entity.getName());
    }

    public MasterCatalogDTO toDto(Ethnicity entity) {
        return (entity == null) ? null : new MasterCatalogDTO(entity.getId(), entity.getName());
    }

    public MasterCatalogDTO toDto(Gender entity) {
        return (entity == null) ? null : new MasterCatalogDTO(entity.getId(), entity.getName());
    }
}