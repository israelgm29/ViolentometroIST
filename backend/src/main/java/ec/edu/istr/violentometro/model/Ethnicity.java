package ec.edu.istr.violentometro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ethnicity")
public class Ethnicity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ethnicity", nullable = false)
    private Integer id;

    @Size(max = 80)
    @NotNull
    @Column(name = "name", nullable = false, length = 80)
    private String name;


}