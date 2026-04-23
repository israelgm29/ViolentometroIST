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

    // Lista de recomendaciones almacenada como texto separado por "|"
    // Ejemplo: "Busca ayuda|Habla con alguien|Llama al 911"
    @Column(name = "recommendations", length = Integer.MAX_VALUE)
    private String recommendations;

    // Helper: convierte el string a lista
    @Transient
    public List<String> getRecommendationList() {
        if (recommendations == null || recommendations.isBlank()) return List.of();
        return List.of(recommendations.split("\\|"));
    }

    // Helper: convierte lista a string para guardar
    public void setRecommendationList(List<String> list) {
        this.recommendations = list == null ? "" : String.join("|", list);
    }
}