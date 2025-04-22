package kz.bdl.erapservice.dto.erap;

import jakarta.xml.bind.Element;
import jakarta.xml.bind.annotation.*;
import kz.bdl.erapservice.dto.FrameData;
import kz.bdl.erapservice.entity.Camera;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import static kz.bdl.erapservice.dto.Constants.DATE_TIME_FORMATTER;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "violation")
public class ErapViolation {

    public void enrich(Camera camera) {
        this.serviceId = camera.getApk().getLocation().getRegion().getVshepData().getServiceId();
        this.clientId = camera.getApk().getLocation().getRegion().getVshepData().getClientId();
        this.messageId = UUID.randomUUID().toString();
        this.sendAt = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        this.districtCode = camera.getApk().getLocation().getRegion().getCode();
        this.source = camera.getApk().getLocation().getRegion().getVshepData().getSource();
        this.deviceNumber = camera.getApk().getDeviceNumber();
        this.certificateNumber = camera.getApk().getCertNumber();
        this.certificateIssueDate = camera.getApk().getCertIssue().format(DATE_TIME_FORMATTER);
        this.certificateExpireDate = camera.getApk().getCertExpiry().format(DATE_TIME_FORMATTER);
    }

    public ErapViolation(String serviceId, String clientId, String messageId, LocalDateTime sendAt, String plateNumber, String violationCode, Integer speed, Integer deltaSpeed, Integer speedLimit, Integer roadLane, String locationId, String locationTitle, String districtCode, String source, String deviceNumber, String certificateNumber, LocalDateTime certificateIssueDate, LocalDateTime certificateExpireDate, LocalDateTime eventTime, FrameData plateFrame, FrameData carFrame, FrameData frame, FrameData videoFrame) {
        this.serviceId = serviceId;
        this.clientId = clientId;
        this.messageId = messageId;
        this.sendAt = sendAt.format(DATE_TIME_FORMATTER);
        this.plateNumber = plateNumber;
        this.violationCode = violationCode;
        this.speed = speed;
        this.deltaSpeed = deltaSpeed;
        this.speedLimit = speedLimit;
        this.roadLane = roadLane;
        this.locationId = locationId;
        this.locationTitle = locationTitle;
        this.districtCode = districtCode;
        this.source = source;
        this.deviceNumber = deviceNumber;
        this.certificateNumber = certificateNumber;
        this.certificateIssueDate = certificateIssueDate.format(DATE_TIME_FORMATTER);
        this.certificateExpireDate = certificateExpireDate.format(DATE_TIME_FORMATTER);
        this.eventTime = eventTime.format(DATE_TIME_FORMATTER);
        this.plateFrame = plateFrame;
        this.carFrame = carFrame;
        this.frame = frame;
        this.videoFrame = videoFrame;
    }

    @XmlElement(name = "service_id")
    private String serviceId;

    @XmlElement(name = "client_id")
    private String clientId;

    @XmlElement(name = "message_id")
    private String messageId;

    @XmlElement(name = "send_at")
    private String sendAt;

    @XmlElement(name = "plate_number")
    private String plateNumber;

    @XmlElement(name = "violation_code")
    private String violationCode;

    @XmlElement(name = "speed")
    private Integer speed;

    @XmlElement(name = "delta_speed")
    private Integer deltaSpeed;

    @XmlElement(name = "speed_limit")
    private Integer speedLimit;

    @XmlElement(name = "road_lane")
    private Integer roadLane;

    @XmlElement(name = "location_id")
    private String locationId;

    @XmlElement(name = "location_title")
    private String locationTitle;

    @XmlElement(name = "district_code")
    private String districtCode;

    @XmlElement(name = "source")
    private String source;

    @XmlElement(name = "device_number")
    private String deviceNumber;

    @XmlElement(name = "certificate_number")
    private String certificateNumber;

    @XmlElement(name = "certificate_issue_date")
    private String certificateIssueDate;

    @XmlElement(name = "certificate_expire_date")
    private String certificateExpireDate;

    @XmlElement(name = "event_time")
    private String eventTime;

    @XmlElement(name = "plate_frame")
    private FrameData plateFrame;

    @XmlElement(name = "car_frame")
    private FrameData carFrame;

    @XmlElement(name = "frame")
    private FrameData frame;

    @XmlElement(name = "video_frame")
    private FrameData videoFrame;
}
