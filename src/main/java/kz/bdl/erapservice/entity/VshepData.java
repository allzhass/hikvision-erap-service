package kz.bdl.erapservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vshep_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VshepData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clientId;

    @Column(nullable = false)
    private String serviceId;

    @Column(nullable = false)
    private String senderId;

    @Column(nullable = false)
    private String senderPwd;

    @Column
    private String source;

    @Lob
    @Column(columnDefinition = "BYTEA")
    private byte[] cert;

    @Column(nullable = false)
    private String certpwd;

    @Column
    private String URL;

    @Column
    private String testUrl;
}
