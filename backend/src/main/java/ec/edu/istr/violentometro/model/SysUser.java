package ec.edu.istr.violentometro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sys_user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SysUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sys_user", nullable = false)
    private Integer id;

    @Column(name = "firsname", nullable = false, length = Integer.MAX_VALUE)
    private String firsname;

    @Column(name = "secondname", length = Integer.MAX_VALUE)
    private String secondname;

    @Column(name = "first_lastname", nullable = false, length = Integer.MAX_VALUE)
    private String firstLastname;

    @Column(name = "second_lastname", length = Integer.MAX_VALUE)
    private String secondLastname;

    @Column(name = "phone", length = Integer.MAX_VALUE)
    private String phone;

    @Column(name = "address", length = Integer.MAX_VALUE)
    private String address;

    @Column(name = "dni", nullable = false, length = Integer.MAX_VALUE)
    private String dni;

    @Column(name = "password", length = Integer.MAX_VALUE)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_role")
    private SysRole idRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_institute")
    private Institute idInstitute;



}