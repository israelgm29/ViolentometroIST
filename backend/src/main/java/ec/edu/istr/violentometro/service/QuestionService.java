package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.components.QuestionMapper;
import ec.edu.istr.violentometro.dto.QuestionDTO;
import ec.edu.istr.violentometro.dto.QuestionZoneDTO;
import ec.edu.istr.violentometro.model.Question;
import ec.edu.istr.violentometro.repository.QuestionRepository;
import ec.edu.istr.violentometro.repository.ViolenceZoneRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final ViolenceZoneRepository zoneRepository;
    private final QuestionMapper questionMapper;



    public List<QuestionDTO> findAll() {
        return questionMapper.toDto(questionRepository.findAll());
    }

    public List<QuestionZoneDTO> findAllWhitZone() {
        return questionMapper.toDtoWithZone(questionRepository.findAllWithZoneEagerly());
    }

    public QuestionDTO findById(Integer id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pregunta no encontrada con ID: " + id));
        return questionMapper.toDto(question);
    }

    @Transactional
    public QuestionDTO save(QuestionDTO dto) {
        // 1. Resolvemos la relación externa (Zone)
        zoneRepository.findById(dto.getIdZone())
                .orElseThrow(() -> new EntityNotFoundException("Zona no encontrada con ID: " + dto.getIdZone()));

        // 2. Convertimos el DTO a Entidad
        Question newQuestion = questionMapper.toEntity(dto);


        // 3. Guardamos y devolvemos el DTO
        return questionMapper.toDto(questionRepository.save(newQuestion));
    }

    @Transactional
    public QuestionDTO update(Integer id, QuestionDTO dto) {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pregunta no encontrada con ID: " + id));

        // 1. Resolvemos la relación externa (Zone) si cambia
        if (dto.getIdZone() != null && !dto.getIdZone().equals(existingQuestion.getIdZone().getId())) {
            zoneRepository.findById(dto.getIdZone())
                    .orElseThrow(() -> new EntityNotFoundException("Zona no encontrada con ID: " + dto.getIdZone()));
            // existingQuestion.setIdZone(zoneEntity); // Asignación si Zone es Entidad
        }

        // 2. Actualizamos los campos
        existingQuestion.setQuestion(dto.getQuestion());
        existingQuestion.setQuestionNumber(dto.getQuestionNumber());
        existingQuestion.setStatus(dto.getStatus());

        return questionMapper.toDto(questionRepository.save(existingQuestion));
    }

    public void deleteById(Integer id) {
        if (!questionRepository.existsById(id)) {
            throw new EntityNotFoundException("Pregunta no encontrada con ID: " + id);
        }
        questionRepository.deleteById(id);
    }
}