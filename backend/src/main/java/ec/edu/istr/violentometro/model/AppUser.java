package ec.edu.istr.violentometro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "app_user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user", nullable = false)
    private Integer id;

    @Column(name = "dni", length = Integer.MAX_VALUE)
    private String dni;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_institute")
    private Institute idInstitute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gender")
    private Gender idGender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_region")
    private Region idRegion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_disability")
    private Disability idDisability;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ethnicity")
    private Ethnicity idEthnicity;

    @Column(name="status")
    private Boolean status;

    @PrePersist
    protected void onCreate() {
        status = true;
    }
}