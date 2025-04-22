package kz.bdl.erapservice.dto.cerebra;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Значение атрибута транспортного средства")
public class VehicleAttributeValue {
    @Schema(example = "белый")
    private String value;
}
