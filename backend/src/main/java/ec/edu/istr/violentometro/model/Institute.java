package ec.edu.istr.violentometro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(name = "institute")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Institute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_instituto", nullable = false)
    private Integer id;

    @Column(name = "code", nullable = false, length = Integer.MAX_VALUE)
    private String code;

    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "short_name", length = Integer.MAX_VALUE)
    private String shortName;

    @Column(name = "address", length = Integer.MAX_VALUE)
    private String address;

    @Column(name = "city", length = Integer.MAX_VALUE)
    private String city;

    @Column(name = "province", length = Integer.MAX_VALUE)
    private String province;

    @Column(name = "country", length = Integer.MAX_VALUE)
    private String country;

    @Column(name = "phone", length = Integer.MAX_VALUE)
    private String phone;

    @Column(name = "email", length = Integer.MAX_VALUE)
    private String email;

    @Column(name = "web_url", length = Integer.MAX_VALUE)
    private String webUrl;

    @Column(name = "config")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> config;

    @Column(name = "status")
    private Boolean status = true;

    @Column(name = "created_date", updatable = false)
    private OffsetDateTime createdDate;

    // ✅ LAZY — no se carga en getAll(), solo cuando se pide explícitamente
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "logo")
    private byte[] logo;

    @Column(name = "logo_content_type", length = 50)
    private String logoContentType;

    @PrePersist
    protected void onCreate() {
        this.createdDate = OffsetDateTime.now();
        if (this.status == null) this.status = true;
    }
}