package ec.edu.istr.violentometro.service;

import ec.edu.istr.violentometro.model.Question;
import ec.edu.istr.violentometro.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService implements BaseService<Question>{

    private QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Question save(Question entity) throws Exception {
        return questionRepository.save(entity);
    }

    @Override
    public List<Question> findAll() throws Exception {
        return questionRepository.findAll();
    }

    @Override
    public Optional<Question> findById(Integer id) throws Exception {
        return questionRepository.findById(id);
    }

    @Override
    public Question updateOne(Question entity, Integer id) throws Exception {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new Exception("Question not found with id " + id));

        existingQuestion.setQuestion(entity.getQuestion());
        existingQuestion.setIdZone(entity.getIdZone());
        existingQuestion.setQuestionNumber(entity.getQuestionNumber());
        existingQuestion.setStatus(entity.getStatus());

        return questionRepository.save(existingQuestion);
    }

    @Override
    public boolean deleteById(Integer id) throws Exception {
        if (!questionRepository.existsById(id)) {
            throw new Exception("Question not found with id " + id);
        }
        questionRepository.deleteById(id);
        return true;
    }
}
