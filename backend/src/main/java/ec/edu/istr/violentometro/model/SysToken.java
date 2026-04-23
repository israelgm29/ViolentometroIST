package ec.edu.istr.violentometro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sys_token")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String token;

    @Column(nullable = false)
    private Boolean expired;

    @Column(nullable = false)
    private Boolean revoked;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sys_user")
    private SysUser sysUser;
}