package ec.edu.istr.violentometro.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "campaign")
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_campaign")
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "excerpt", columnDefinition = "TEXT")
    private String excerpt;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "external_url")
    private String externalUrl;

    @Column(name = "featured")
    private Boolean featured = false;

    @Column(name = "status")
    private Boolean status = true;

    @Column(name = "publish_date")
    private LocalDate publishDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_category")
    private CampaignCategory category;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}