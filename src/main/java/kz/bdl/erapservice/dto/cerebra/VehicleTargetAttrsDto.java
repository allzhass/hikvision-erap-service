package kz.bdl.erapservice.dto.cerebra;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Schema(description = "Атрибуты цели (контекста)")
public class VehicleTargetAttrsDto {

    private String IACPicUp;
    private String alarmId;
    private String alarmType;
    private String areaCode;
    private String cameraAddress;
    private String cameraIndexCode;
    private String cameraName;
    private String cameraType;
    private String cascade;
    private String crossingExternalCode;
    private Integer crossingId;
    private String crossingIndexCode;
    private String crossingName;
    private String deviceIndexCode;
    private String deviceLatitude;
    private String deviceLongitude;
    private String deviceName;
    private String deviceType;
    private String directionIndex;
    private String imageServerCode;
    private Integer laneNo;
    private String passID;

    @Schema(example = "2024-04-02T11:59:45Z")
    private LocalDateTime passTime;

    private String platePicUrl;
    private Integer recognitionSign;
    private String regionIndexCode;
    private String uuid;
    private ResolutionDto vehicleBackgroundImageResolution;
    private String vehicleColorDepth;
    private Integer vehicleLen;
    private Integer vehiclePicNum;
    private String vehiclePicUrl1;
    private String vehiclePicUrl2;
    private String vehiclePicUrl3;
    private Integer vehicleSpeed;
    private String xmlBuf;
    private String vehicleVideoUrl;
}