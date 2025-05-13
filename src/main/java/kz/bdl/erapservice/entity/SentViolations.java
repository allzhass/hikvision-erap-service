package kz.bdl.erapservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sent_violations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentViolations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "camera_violation_id", nullable = false)
    private CameraViolation cameraViolation;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String request;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String response;

    private Boolean isError;
    private LocalDateTime createdAt;
}
