package ec.edu.istr.violentometro.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "quiz_result")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_result")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private AppUser idAppUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_survey", nullable = false)
    private Survey idSurvey;

    @Column(name = "total_score")
    private Integer totalScore; // Suma de puntos o respuestas afirmativas

    @Column(name = "risk_level")
    private String riskLevel; // 'low', 'medium', 'high', 'critical'

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dominant_zone_id")
    private ViolenceZone dominantZone;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}