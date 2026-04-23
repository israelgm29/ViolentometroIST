package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.dto.QuizResultDTO;
import ec.edu.istr.violentometro.dto.QuizResultRequest;
import ec.edu.istr.violentometro.model.*;
import ec.edu.istr.violentometro.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuizResultService {

    private final QuizResultRepository quizResultRepository;
    private final AppUserRepository appUserRepository;
    private final SurveyRepository surveyRepository;
    private final ViolenceZoneRepository violenceZoneRepository;

    @Transactional
    public QuizResultDTO saveResult(QuizResultDTO dto) {
        AppUser user = appUserRepository.findById(dto.getIdAppUser())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Survey survey = surveyRepository.findById(dto.getIdSurvey())
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));

        QuizResult result = new QuizResult();
        result.setIdAppUser(user);
        result.setIdSurvey(survey);
        result.setTotalScore(dto.getTotalScore());
        result.setRiskLevel(dto.getRiskLevel());

        if (dto.getDominantZoneId() != null) {
            ViolenceZone zone = violenceZoneRepository.findById(dto.getDominantZoneId()).orElse(null);
            result.setDominantZone(zone);
        }

        quizResultRepository.save(result);
        return dto;
    }

    @Transactional
    public void saveFinalResult(QuizResultRequest dto) {
        AppUser user = appUserRepository.findById(dto.getIdAppUser())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Survey survey = surveyRepository.findById(dto.getIdSurvey())
                .orElseThrow(() -> new RuntimeException("Cuestionario no encontrado"));

        QuizResult result = new QuizResult();
        result.setIdAppUser(user);
        result.setIdSurvey(survey);
        result.setTotalScore(dto.getTotalScore());
        result.setRiskLevel(dto.getRiskLevel());

        if (dto.getDominantZoneId() != null) {
            result.setDominantZone(violenceZoneRepository.findById(dto.getDominantZoneId()).orElse(null));
        }

        quizResultRepository.save(result);
    }
}