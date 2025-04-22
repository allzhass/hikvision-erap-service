package kz.bdl.erapservice.dto.cerebra;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Номерной знак")
public class PlateNoDto {
    @Schema(example = "A123BC01")
    private String value;

    @Schema(example = "0.98")
    private Double confidence;
}
