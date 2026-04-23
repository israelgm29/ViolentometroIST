package ec.edu.istr.violentometro.repository;

import ec.edu.istr.violentometro.model.CampaignCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignCategoryRepository extends JpaRepository<CampaignCategory, Integer> {
    List<CampaignCategory> findByStatusTrue();
}