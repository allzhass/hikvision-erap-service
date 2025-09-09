package kz.bdl.erapservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id")
    private String externalId;

    @ManyToOne
    @JoinColumn(name = "camera_id", nullable = false)
    private Camera camera;

    @ManyToOne
    @JoinColumn(name = "violation_id")
    private Violation violation;

    @Column(name = "plate_number")
    private String plateNumber;

    @Column(name = "speed")
    private Integer speed;

    @Column(name = "speed_limit")
    private Integer speedLimit;

    @Column(name = "road_lane")
    private Integer roadLane;

    @Column(name = "camera_direction")
    private Integer cameraDirection;

    @Column(columnDefinition = "TEXT")
    private String image;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
