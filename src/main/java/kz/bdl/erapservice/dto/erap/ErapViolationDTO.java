package kz.bdl.erapservice.dto.erap;

import io.swagger.v3.oas.annotations.media.Schema;
import kz.bdl.erapservice.dto.FrameData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Violation data to send to ERAP")
public class ErapViolationDTO {
    private String serviceId;
    private String clientId;
    private String messageId;
    private LocalDateTime sendAt;
    private String plateNumber;
    private String violationCode;
    private Integer speed;
    private Integer deltaSpeed;
    private Integer speedLimit;
    private Integer roadLane;
    private String locationId;
    private String locationTitle;
    private String districtCode;
    private String source;
    private String deviceNumber;
    private String certificateNumber;
    private LocalDateTime certificateIssueDate;
    private LocalDateTime certificateExpireDate;
    private LocalDateTime eventTime;
    private FrameData plateFrame;
    private FrameData carFrame;
    private FrameData frame;
    private FrameData videoFrame;
}
