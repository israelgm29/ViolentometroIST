package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.dto.AppUserDTO;
import ec.edu.istr.violentometro.model.AppUser;
import ec.edu.istr.violentometro.model.Institute;
import ec.edu.istr.violentometro.repository.AppUserRepository;
import ec.edu.istr.violentometro.repository.InstituteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppUserService implements BaseService<AppUser> {

    private final AppUserRepository appUserRepository;
    private final InstituteRepository instituteRepository;


    @Autowired
    public AppUserService(AppUserRepository appUserRepository, InstituteRepository instituteRepository) {
        this.appUserRepository = appUserRepository;
        this.instituteRepository = instituteRepository;
    }

    @Override
    public AppUser save(AppUser entity) throws Exception {

        return appUserRepository.save(entity);
    }

    @Override
    public List<AppUser> findAll() throws Exception {
        return appUserRepository.findAll();
    }

    @Override
    public Optional<AppUser> findById(Integer id) {
        return appUserRepository.findById(id);
    }

    public Optional<AppUser> findByDni(String dni) {
        return appUserRepository.findByDni(dni);
    }

    @Override
    public AppUser updateOne(AppUser entity, Integer id) throws Exception {

        AppUser existingAppUser = appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AppUser not found with id " + id));

        existingAppUser.setDni(entity.getDni());

        if (entity.getIdInstitute() != null && entity.getIdInstitute().getId() != null) {
            Institute updatedInstitute = instituteRepository.findById(entity.getIdInstitute().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Institute not found with id " + entity.getIdInstitute().getId()));
            existingAppUser.setIdInstitute(updatedInstitute);
        }
        return appUserRepository.save(existingAppUser);
    }

    @Override
    public boolean deleteById(Integer id) throws Exception {
        if (!appUserRepository.existsById(id)) {
            throw new EntityNotFoundException("AppUser not found with id " + id);
        }
        appUserRepository.deleteById(id);
        return true;
    }

    public AppUser updateOneFromDto(Integer id, AppUserDTO requestDTO) {

        AppUser existingAppUser = appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AppUser not found with id " + id));

        existingAppUser.setDni(requestDTO.getDni());

        if (requestDTO.getIdInstitute() != null) {
            Institute updatedInstitute = instituteRepository.findById(requestDTO.getIdInstitute())
                    .orElseThrow(() -> new EntityNotFoundException("Institute not found with id " + requestDTO.getIdInstitute()));
            existingAppUser.setIdInstitute(updatedInstitute);
        }

        return appUserRepository.save(existingAppUser);
    }

    public AppUser createFromDto(AppUserDTO requestDTO) {

        Institute institute = instituteRepository.findById(requestDTO.getIdInstitute())
                .orElseThrow(() -> new EntityNotFoundException("Institute not found with id " + requestDTO.getIdInstitute()));

        AppUser appUser = new AppUser();
        appUser.setDni(requestDTO.getDni());
        appUser.setIdInstitute(institute);

        return appUserRepository.save(appUser);
    }
}