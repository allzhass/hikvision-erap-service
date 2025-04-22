package kz.bdl.erapservice.dto.cerebra;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Разрешение изображения")
public class ResolutionDto {
    @Schema(example = "1920")
    private Integer width;

    @Schema(example = "1080")
    private Integer height;
}
