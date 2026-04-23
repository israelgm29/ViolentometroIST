package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.components.CatalogMapper;
import ec.edu.istr.violentometro.dto.MasterCatalogDTO;
import ec.edu.istr.violentometro.model.Ethnicity;
import ec.edu.istr.violentometro.repository.EthnicityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EthnicityService {
    private final EthnicityRepository repository;
    private final CatalogMapper mapper;

    public List<MasterCatalogDTO> findAll() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }
    @Transactional
    public MasterCatalogDTO save(MasterCatalogDTO dto) {
        Ethnicity entity = new Ethnicity();
        if (dto.getId() != null) { // Si viene ID, es una actualización
            entity = repository.findById(dto.getId()).orElseThrow();
        }
        entity.setName(dto.getName());
        return mapper.toDto(repository.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}