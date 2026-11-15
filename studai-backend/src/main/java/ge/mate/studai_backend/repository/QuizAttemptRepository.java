package ge.mate.studai_backend.repository;

import ge.mate.studai_backend.domain.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    java.util.List<QuizAttempt> findByUserIdOrderByCompletedAtDesc(Long userId);
    java.util.Optional<QuizAttempt> findFirstByUserIdAndQuizIdOrderByCompletedAtDesc(Long userId, Long quizId);
    void deleteByQuizId(Long quizId);
}
