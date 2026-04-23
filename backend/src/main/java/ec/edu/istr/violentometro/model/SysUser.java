package ec.edu.istr.violentometro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "sys_user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SysUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sys_user", nullable = false)
    private Integer id;

    @Column(name = "firstname", nullable = false, length = Integer.MAX_VALUE)
    private String firstname;

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

    @Column(name = "status")
    private Boolean status;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "email", unique = true, length = 255)
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_role")
    private SysRole idRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_institute")
    private Institute idInstitute;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
        status    = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // ── UserDetails ───────────────────────────────────────────────────────────

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // idRole.getName() debe devolver "ROLE_ADMIN" o "ROLE_ANALYST"
        return List.of(new SimpleGrantedAuthority(idRole.getName()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    // Spring Security usa este método como identificador — usamos email
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Respeta el campo status de la BD — usuario inactivo no puede loguear
        return Boolean.TRUE.equals(status);
    }
}