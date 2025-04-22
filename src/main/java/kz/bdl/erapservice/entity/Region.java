package kz.bdl.erapservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "region")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vshep_data_id")
    private VshepData vshepData;

    @Column(nullable = false, unique = true)
    private String code;

    @Column
    private String nameRu;

    @Column
    private String nameKz;

    @Column
    private String nameEn;
}
