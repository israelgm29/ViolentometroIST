package ec.edu.istr.violentometro.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "campaign_category")
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class CampaignCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_category")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "color")
    private String color;

    @Column(name = "icon")
    private String icon;

    @Column(name = "status")
    private Boolean status = true;
}