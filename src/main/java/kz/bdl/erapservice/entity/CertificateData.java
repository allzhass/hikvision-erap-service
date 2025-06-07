package kz.bdl.erapservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "certificate_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String issuedBy;

    @Column(nullable = false)
    private String issuedAt;

    @Column(nullable = false)
    private String expiresAt;

    @Column(nullable = false)
    private String certpwd;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String cert;
}
