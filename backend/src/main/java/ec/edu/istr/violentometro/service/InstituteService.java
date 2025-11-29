package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.model.Institute;
import ec.edu.istr.violentometro.repository.InstituteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstituteService implements BaseService<Institute>{

    private InstituteRepository instituteRepository;
    public InstituteService(InstituteRepository instituteRepository) {
        this.instituteRepository = instituteRepository;
    }

    @Override
    public Institute save(Institute entity) throws Exception {
     return instituteRepository.save(entity);
    }

    @Override
    public List<Institute> findAll() throws Exception {
        return instituteRepository.findAll();
    }

    @Override
    public Optional<Institute> findById(Integer id) throws Exception {
        return instituteRepository.findById(id);
    }

    @Override
    public Institute updateOne(Institute entity, Integer id) throws Exception {
        Institute existingInstitute = instituteRepository.findById(id)
                .orElseThrow(() -> new Exception("Institute not found with id " + id));

        existingInstitute.setName(entity.getName());
        existingInstitute.setAddress(entity.getAddress());
        existingInstitute.setPhone(entity.getPhone());

        return instituteRepository.save(existingInstitute);
    }

    @Override
    public boolean deleteById(Integer id) throws Exception {
        if (!instituteRepository.existsById(id)) {
            throw new Exception("Institute not found with id " + id);
        }
        instituteRepository.deleteById(id);
        return true;
    }
}
