package ec.edu.istr.violentometro.repository;

import ec.edu.istr.violentometro.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Integer> {
    @Modifying
    @Query("UPDATE Survey s SET s.isActive = false")
    void deactivateAllSurveys();

    Optional<Survey> findByIsActiveTrue();
}
