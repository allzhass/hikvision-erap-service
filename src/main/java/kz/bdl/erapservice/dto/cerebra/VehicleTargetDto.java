package kz.bdl.erapservice.dto.cerebra;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Информация о цели транспортного средства")
public class VehicleTargetDto {
    @Schema(description = "Границы цели на изображении")
    private RectangleDto rect;

    @Schema(description = "Данные о транспортном средстве")
    private VehicleDetailsDto vehicle;
}
