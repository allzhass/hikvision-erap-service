package kz.bdl.erapservice.mapper;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import kz.bdl.erapservice.dto.FrameData;
import kz.bdl.erapservice.dto.RequestDTO;
import kz.bdl.erapservice.dto.cerebra.CerebraRequestDTO;
import kz.bdl.erapservice.dto.erap.ErapViolation;
import kz.bdl.erapservice.entity.Camera;
import kz.bdl.erapservice.entity.CameraViolation;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

public class ViolationMapper {
    public static ErapViolation fromBasicViolation(RequestDTO requestDTO, CameraViolation cameraViolation) {
        return new ErapViolation(
                cameraViolation.getCamera().getApk().getLocation().getRegion().getVshepData().getServiceId(),
                cameraViolation.getCamera().getApk().getLocation().getRegion().getVshepData().getClientId(),
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                requestDTO.getPlateNumber(),
                cameraViolation.getViolation().getCode(),
                requestDTO.getSpeed(),
                requestDTO.getDeltaSpeed(),
                requestDTO.getSpeedLimit(),
                requestDTO.getRoadLane(),
                new String(cameraViolation.getCamera().getApk().getLocation().getId().toString()),
                cameraViolation.getCamera().getDirection(),
                cameraViolation.getCamera().getApk().getLocation().getRegion().getCode(),
                cameraViolation.getCamera().getApk().getLocation().getRegion().getVshepData().getSource(),
                cameraViolation.getCamera().getApk().getDeviceNumber(),
                cameraViolation.getCamera().getApk().getCertNumber(),
                cameraViolation.getCamera().getApk().getCertIssue(),
                cameraViolation.getCamera().getApk().getCertExpiry(),
                requestDTO.getEventTime(),
                requestDTO.getPlateFrame(),
                requestDTO.getCarFrame(),
                requestDTO.getFrame(),
                requestDTO.getVideoFrame()
        );
    }

    public static ErapViolation fromCerebraViolation(
            CerebraRequestDTO requestDTO,
            CameraViolation cameraViolation,
            FrameData plateFrame, FrameData carFrame, FrameData frameData, FrameData videoFrame) {
        return new ErapViolation(
                cameraViolation.getCamera().getApk().getLocation().getRegion().getVshepData().getServiceId(),
                cameraViolation.getCamera().getApk().getLocation().getRegion().getVshepData().getClientId(),
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                requestDTO.getVehicleAlarmResult().get(0).getTarget().get(0).getVehicle().getPlateNo().getValue(),
                cameraViolation.getViolation().getCode(),
                requestDTO.getVehicleAlarmResult().get(0).getTargetAttrs().getVehicleSpeed(),
                requestDTO.getVehicleAlarmResult().get(0).getTargetAttrs().getVehicleSpeed(), //.getDeltaSpeed(),
                requestDTO.getVehicleAlarmResult().get(0).getTargetAttrs().getVehicleSpeed(), //.getSpeedLimit(),
                requestDTO.getVehicleAlarmResult().get(0).getTargetAttrs().getLaneNo(), //.getRoadLane(),
                new String(cameraViolation.getCamera().getApk().getLocation().getId().toString()),
                cameraViolation.getCamera().getDirection(),
                cameraViolation.getCamera().getApk().getLocation().getRegion().getCode(),
                cameraViolation.getCamera().getApk().getLocation().getRegion().getVshepData().getSource(),
                cameraViolation.getCamera().getApk().getDeviceNumber(),
                cameraViolation.getCamera().getApk().getCertNumber(),
                cameraViolation.getCamera().getApk().getCertIssue(),
                cameraViolation.getCamera().getApk().getCertExpiry(),
                requestDTO.getVehicleAlarmResult().get(0).getTargetAttrs().getPassTime(), //.getEventTime(),
                plateFrame,
                carFrame,
                frameData,
                videoFrame
        );
    }

    public static String toXmlString(ErapViolation erapViolation) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ErapViolation.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            java.io.StringWriter writer = new java.io.StringWriter();
            marshaller.marshal(erapViolation, writer);
            String xmlString = writer.toString();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc =  builder.parse(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));
            return DocumentMapper.getString(doc);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}