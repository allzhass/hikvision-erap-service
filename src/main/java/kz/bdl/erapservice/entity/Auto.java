package kz.bdl.erapservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plate_number", nullable = false)
    private String plateNumber;

    @Column(name = "description")
    private String description;

    @Column(name = "is_send_violation", nullable = false)
    private Boolean isSendViolation = false;
}