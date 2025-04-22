package kz.bdl.erapservice.dto.cerebra;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Детали транспортного средства")
public class VehicleDetailsDto {
    @Schema(example = "true", description = "Является ли основным автомобилем")
    private String isMainVehicle;

    private VehicleAttributeValue dangmark;
    private VehicleAttributeValue envprosign;
    private VehicleAttributeValue pendant;
    private VehicleAttributeValue pilotSafebelt;
    private VehicleAttributeValue pilotSunvisor;
    private VehicleAttributeValue plateColor;
    private PlateNoDto plateNo;
    private RectangleDto plateRect;
    private VehicleAttributeValue plateType;
    private VehicleAttributeValue uphone;
    private VehicleAttributeValue vehicleColor;
    private VehicleAttributeValue vehicleHead;
    private VehicleAttributeValue vehicleLogo;
    private VehicleAttributeValue vehicleModel;
    private VehicleAttributeValue vehicleSubLogo;
    private VehicleAttributeValue vehicleType;
    private VehicleAttributeValue vicePilotSafebelt;
    private VehicleAttributeValue vicePilotSunvisor;
}
