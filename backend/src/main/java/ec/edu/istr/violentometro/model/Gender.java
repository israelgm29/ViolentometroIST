package ec.edu.istr.violentometro.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "gender")
public class Gender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_gender", nullable = false)
    private Integer id;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;


}