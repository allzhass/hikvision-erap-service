package kz.bdl.erapservice.dto.cerebra;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Прямоугольная область")
public class RectangleDto {

    @Schema(example = "50.0")
    private Double x;

    @Schema(example = "60.0")
    private Double y;

    @Schema(example = "200.0")
    private Double width;

    @Schema(example = "100.5")
    private Double height;
}