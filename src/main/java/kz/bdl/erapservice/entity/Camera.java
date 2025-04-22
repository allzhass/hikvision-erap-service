package kz.bdl.erapservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "camera")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Camera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "apk_id", nullable = false)
    private APK apk;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String ip;

    private String code;
    private String direction;
    private String brand;
    private Integer collectionDays;
}
