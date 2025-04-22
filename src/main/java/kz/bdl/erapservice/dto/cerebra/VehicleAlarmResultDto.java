package kz.bdl.erapservice.dto.cerebra;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Результат тревоги по транспортному средству")
public class VehicleAlarmResultDto {
    @Schema(description = "Цели (автомобили)")
    private List<VehicleTargetDto> target;

    @Schema(description = "Атрибуты цели")
    private VehicleTargetAttrsDto targetAttrs;

    @Schema(description = "URL изображения цели", example = "http://image-server/target.jpg")
    private String targetPicUrl;

    @Schema(description = "ID задачи", example = "TASK001")
    private String taskID;
}
