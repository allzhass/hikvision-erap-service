package kz.bdl.erapservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "apk")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class APK {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(unique = true)
    private String deviceNumber;

    @Column(unique = true)
    private String certNumber;

    private LocalDateTime certIssue;
    private LocalDateTime certExpiry;
}

