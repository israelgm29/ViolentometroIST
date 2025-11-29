package ec.edu.istr.violentometro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetTime;

@Entity
@Table(name = "user_answer")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_answer", nullable = false)
    private Integer id;

    @Column(name = "id_app_user")
    private Integer idAppUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_question")
    private Question idQuestion;

    @Column(name = "aswer")
    private Boolean aswer;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private OffsetTime createdAt;


}