package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.dto.QuestionDTO;
import ec.edu.istr.violentometro.model.ViolenceZone;
import ec.edu.istr.violentometro.repository.ViolenceZoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ViolenceZoneService implements BaseService<ViolenceZone> {

    private ViolenceZoneRepository violenceZoneRepository;

    public ViolenceZoneService(ViolenceZoneRepository violenceZoneRepository) {
        this.violenceZoneRepository = violenceZoneRepository;
    }

    @Override
    public ViolenceZone save(ViolenceZone entity) throws Exception {
     return violenceZoneRepository.save(entity);
    }

    @Override
    public List<ViolenceZone> findAll() throws Exception {
        return  violenceZoneRepository.findAll();
    }

    @Override
    public Optional<ViolenceZone> findById(Integer id) throws Exception {
        return violenceZoneRepository.findById(id);
    }

    @Override
    public ViolenceZone updateOne(ViolenceZone entity, Integer id) throws Exception {
        ViolenceZone existingViolenceZone = violenceZoneRepository.findById(id)
                .orElseThrow(() -> new Exception("ViolenceZone not found with id " + id));

        existingViolenceZone.setName(entity.getName());
        existingViolenceZone.setDescription(entity.getDescription());
        existingViolenceZone.setColor(entity.getColor());
        existingViolenceZone.setSeverity(entity.getSeverity());

        return violenceZoneRepository.save(existingViolenceZone);
    }

    @Override
    public boolean deleteById(Integer id) throws Exception {
        if (!violenceZoneRepository.existsById(id)) {
            throw new Exception("ViolenceZone not found with id " + id);
        }
        violenceZoneRepository.deleteById(id);
        return true;
    }


}
