package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.components.CampaignMapper;
import ec.edu.istr.violentometro.dto.CampaignCategoryDTO;
import ec.edu.istr.violentometro.dto.CampaignDTO;
import ec.edu.istr.violentometro.model.Campaign;
import ec.edu.istr.violentometro.model.CampaignCategory;
import ec.edu.istr.violentometro.repository.CampaignCategoryRepository;
import ec.edu.istr.violentometro.repository.CampaignRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignCategoryRepository categoryRepository;
    private final CampaignMapper campaignMapper;

    // ── CAMPAÑAS ─────────────────────────────────────────────
    public List<CampaignDTO> findAll() {
        return campaignRepository.findAll().stream()
                .map(campaignMapper::toDto).toList();
    }

    public List<CampaignDTO> findActive() {
        return campaignRepository.findByStatusTrueOrderByPublishDateDesc()
                .stream().map(campaignMapper::toDto).toList();
    }

    public CampaignDTO findById(Integer id) {
        return campaignMapper.toDto(campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaña no encontrada: " + id)));
    }

    @Transactional
    public CampaignDTO save(CampaignDTO dto) {
        CampaignCategory cat = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        // Si se marca como featured, quitar featured de las demás
        if (Boolean.TRUE.equals(dto.getFeatured())) {
            campaignRepository.findFirstByFeaturedTrueAndStatusTrue()
                    .ifPresent(c -> { c.setFeatured(false); campaignRepository.save(c); });
        }

        Campaign campaign = campaignMapper.toEntity(dto, cat);
        return campaignMapper.toDto(campaignRepository.save(campaign));
    }

    @Transactional
    public CampaignDTO update(Integer id, CampaignDTO dto) {
        Campaign existing = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaña no encontrada: " + id));

        CampaignCategory cat = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        if (Boolean.TRUE.equals(dto.getFeatured()) && !existing.getFeatured()) {
            campaignRepository.findFirstByFeaturedTrueAndStatusTrue()
                    .ifPresent(c -> { c.setFeatured(false); campaignRepository.save(c); });
        }

        existing.setTitle(dto.getTitle());
        existing.setExcerpt(dto.getExcerpt());
        existing.setImageUrl(dto.getImageUrl());
        existing.setExternalUrl(dto.getExternalUrl());
        existing.setFeatured(dto.getFeatured());
        existing.setStatus(dto.getStatus());
        existing.setPublishDate(dto.getPublishDate());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setCategory(cat);

        return campaignMapper.toDto(campaignRepository.save(existing));
    }

    @Transactional
    public void toggleStatus(Integer id) {
        Campaign c = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaña no encontrada: " + id));
        c.setStatus(!c.getStatus());
        campaignRepository.save(c);
    }

    public void delete(Integer id) {
        campaignRepository.deleteById(id);
    }

    // ── CATEGORÍAS ────────────────────────────────────────────
    public List<CampaignCategoryDTO> findAllCategories() {
        return categoryRepository.findAll().stream()
                .map(campaignMapper::toCategoryDto).toList();
    }

    public List<CampaignCategoryDTO> findActiveCategories() {
        return categoryRepository.findByStatusTrue().stream()
                .map(campaignMapper::toCategoryDto).toList();
    }

    @Transactional
    public CampaignCategoryDTO saveCategory(CampaignCategoryDTO dto) {
        CampaignCategory cat = new CampaignCategory();
        cat.setName(dto.getName());
        cat.setColor(dto.getColor());
        cat.setIcon(dto.getIcon());
        cat.setStatus(true);
        return campaignMapper.toCategoryDto(categoryRepository.save(cat));
    }

    @Transactional
    public CampaignCategoryDTO updateCategory(Integer id, CampaignCategoryDTO dto) {
        CampaignCategory cat = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + id));
        cat.setName(dto.getName());
        cat.setColor(dto.getColor());
        cat.setIcon(dto.getIcon());
        cat.setStatus(dto.getStatus());
        return campaignMapper.toCategoryDto(categoryRepository.save(cat));
    }
}