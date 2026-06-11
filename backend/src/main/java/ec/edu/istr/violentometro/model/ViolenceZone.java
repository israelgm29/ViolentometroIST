package ec.edu.istr.violentometro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "violence_zone")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ViolenceZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_zone", nullable = false)
    private Integer id;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "color", length = Integer.MAX_VALUE)
    private String color;

    @Column(name = "severity")
    private Integer severity;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "result_title", length = 255)
    private String resultTitle;

    @Column(name = "result_message", length = Integer.MAX_VALUE)
    private String resultMessage;

    @Column(name = "recommendations", length = Integer.MAX_VALUE)
    private String recommendations;

    // ── NUEVO: relación con Institute (nullable = es plantilla global) ──
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_instituto", nullable = true)
    private Institute institute;

    // ── NUEVO: indica si es plantilla base copiable ──────────────────────
    @Column(name = "is_template", nullable = false)
    private Boolean isTemplate = false;
    // ─────────────────────────────────────────────────────────────────────

    @Transient
    public List<String> getRecommendationList() {
        if (recommendations == null || recommendations.isBlank()) return List.of();
        return List.of(recommendations.split("\\|"));
    }

    public void setRecommendationList(List<String> list) {
        this.recommendations = list == null ? "" : String.join("|", list);
    }
}