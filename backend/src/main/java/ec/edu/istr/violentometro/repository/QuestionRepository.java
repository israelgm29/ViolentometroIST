package ec.edu.istr.violentometro.repository;

import ec.edu.istr.violentometro.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

    // Método para obtener todas las preguntas con sus zonas de violencia cargadas
    @Query("SELECT q FROM Question q JOIN FETCH q.idZone ORDER BY q.id ASC")
    List<Question> findAllWithZoneEagerly();

    // Método para obtener preguntas por ID de cuestionario ordenadas por número de pregunta
    List<Question> findBySurveyIdOrderByQuestionNumberAsc(Integer surveyId);

    @Modifying
    @Query("DELETE FROM Question q WHERE q.survey.id = :surveyId")
    void deleteBySurveyId(@Param("surveyId") Integer surveyId);
}
