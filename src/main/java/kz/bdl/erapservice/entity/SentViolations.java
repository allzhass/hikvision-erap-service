package kz.bdl.erapservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sent_violations")
@Data
@AllArgsConstructor
public class SentViolations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "camera_violation_id")
    private CameraViolation cameraViolation;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String request;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String response;

    private Boolean isError;
    private LocalDateTime createdAt;
    private String plateNumber;
    private String messageId;

    public SentViolations() {
        this.request = "";
        this.response = "";
    }

    public void setRequest(String request) {
        this.request = new StringBuilder().append(this.request).append("\n-------------------\n").append(request).toString();
    }

    public void setResponse(String response) {
        this.response = new StringBuilder().append(this.response).append("\n-------------------\n").append(response).toString();
    }

    @Override
    public String toString() {
        return "SentViolations{" +
                "id=" + id +
                ", cameraViolation=" + cameraViolation +
                ", isError=" + isError +
                ", createdAt=" + createdAt +
                ", plateNumber='" + plateNumber + '\'' +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}
