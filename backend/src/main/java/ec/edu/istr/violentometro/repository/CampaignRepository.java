package ec.edu.istr.violentometro.repository;

import ec.edu.istr.violentometro.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Integer> {
    List<Campaign> findByStatusTrueOrderByPublishDateDesc();
    List<Campaign> findByCategoryIdAndStatusTrueOrderByPublishDateDesc(Integer categoryId);
    Optional<Campaign> findFirstByFeaturedTrueAndStatusTrue();
}
