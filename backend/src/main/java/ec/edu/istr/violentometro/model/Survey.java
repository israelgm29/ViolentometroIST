package ec.edu.istr.violentometro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.OffsetDateTime;

@Entity
@Table(name = "survey")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_survey")
    private Integer id;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    // ── NUEVO: relación con Institute ─────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_instituto", nullable = false)
    private Institute institute;
    // ─────────────────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        if (isActive == null) isActive = false;
    }
}