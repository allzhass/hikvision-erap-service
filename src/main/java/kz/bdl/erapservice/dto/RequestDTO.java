package kz.bdl.erapservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to controller")
public class RequestDTO {
    private String cameraCode;
    private String plateNumber;
    private String violationCode;
    private Integer speed;
    private Integer deltaSpeed;
    private Integer speedLimit;
    private Integer roadLane;
    private LocalDateTime eventTime;
    private FrameData plateFrame;
    private FrameData carFrame;
    private FrameData frame;
    private FrameData videoFrame;
}
