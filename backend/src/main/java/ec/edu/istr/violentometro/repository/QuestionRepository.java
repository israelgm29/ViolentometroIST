package ec.edu.istr.violentometro.repository;

import ec.edu.istr.violentometro.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
}
