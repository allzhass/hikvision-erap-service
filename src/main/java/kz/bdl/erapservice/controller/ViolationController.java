package kz.bdl.erapservice.controller;

import jakarta.xml.bind.JAXBException;
import kz.bdl.erapservice.dto.FrameData;
import kz.bdl.erapservice.dto.RequestDTO;
import kz.bdl.erapservice.dto.cerebra.CerebraRequestDTO;
import kz.bdl.erapservice.dto.erap.ErapViolation;
import kz.bdl.erapservice.dto.vshep.request.Envelope;
import kz.bdl.erapservice.dto.vshep.response.Response;
import kz.bdl.erapservice.dto.vshep.response.VshepStatus;
import kz.bdl.erapservice.entity.CameraViolation;
import kz.bdl.erapservice.entity.SentViolations;
import kz.bdl.erapservice.external.FileDownloadWebClient;
import kz.bdl.erapservice.mapper.ViolationMapper;
import kz.bdl.erapservice.mapper.VshepMapper;
import kz.bdl.erapservice.service.ViolationService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping("/violation")
public class ViolationController {
    private ViolationService violationService;
    private FileDownloadWebClient fileDownloadWebClient;

    @PostMapping
    public ResponseEntity<Response> basicViolation(
            @RequestBody RequestDTO requestDTO) {

        SentViolations sentViolations = new SentViolations();
        CameraViolation cameraViolation = violationService.checkViolation(requestDTO.getCameraCode(), requestDTO.getViolationCode(), sentViolations);

        ErapViolation erapViolation = ViolationMapper.fromBasicViolation(requestDTO, cameraViolation);
        String resultString = violationService.sendViolation(erapViolation, sentViolations);

        Response response;
        try {
            response = VshepMapper.toEnvelope(resultString).getBody().getSendMessageResponse().getResponse();
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        
        if ((response != null)
                && (VshepStatus.OK.equals(response.getResponseInfo().getStatus().getCode()))) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/cerebra")
    public ResponseEntity<Response> cerebraViolation(
            @RequestBody CerebraRequestDTO requestDTO) {

        SentViolations sentViolations = new SentViolations();
        CameraViolation cameraViolation = violationService.checkViolation(
                requestDTO.getIpAddress(),
                requestDTO.getVehicleAlarmResult().get(0).getTargetAttrs().getAlarmType(),
                sentViolations);

        String platePic = fileDownloadWebClient.downloadFileAsBase64(URI.create(requestDTO.getVehicleAlarmResult().get(0).getTargetAttrs().getPlatePicUrl()));
        String carPic1 = fileDownloadWebClient.downloadFileAsBase64(URI.create(requestDTO.getVehicleAlarmResult().get(0).getTargetAttrs().getVehiclePicUrl1()));
        String carPic2 = fileDownloadWebClient.downloadFileAsBase64(URI.create(requestDTO.getVehicleAlarmResult().get(0).getTargetAttrs().getVehiclePicUrl2()));
        String carVideo = fileDownloadWebClient.downloadFileAsBase64(URI.create(requestDTO.getVehicleAlarmResult().get(0).getTargetAttrs().getVehicleVideoUrl()));

        ErapViolation erapViolation = ViolationMapper.fromCerebraViolation(
                requestDTO,
                cameraViolation,
                new FrameData(platePic, "jpg"),
                new FrameData(carPic1, "jpg"),
                new FrameData(carPic2, "jpg"),
                new FrameData(carVideo, "mp4")
        );
        String resultString = violationService.sendViolation(erapViolation, sentViolations);

        Response response;
        try {
            response = VshepMapper.toEnvelope(resultString).getBody().getSendMessageResponse().getResponse();
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        if ((response != null)
                && (VshepStatus.OK.equals(response.getResponseInfo().getStatus().getCode()))) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping(path = "/erap", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.TEXT_XML_VALUE)
    public ResponseEntity<String> receiveSoapRequest(@RequestBody Envelope envelope) {
        ErapViolation erapViolation = envelope.getBody().getSendMessage().getRequest().getRequestData().getData().getOnEventShep().getViolation();

        SentViolations sentViolations = new SentViolations();
        CameraViolation cameraViolation = violationService.checkViolation(erapViolation.getLocationTitle(), erapViolation.getViolationCode(), sentViolations);
        erapViolation.enrich(cameraViolation.getCamera());
        String resultString = violationService.sendViolation(erapViolation, sentViolations);

        kz.bdl.erapservice.dto.vshep.response.Envelope envelopeResponse;
        try {
            envelopeResponse = VshepMapper.toEnvelope(resultString);
        } catch (JAXBException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(resultString);
        }

        try {
            if (VshepStatus.OK.equals(
                    envelopeResponse.getBody().getSendMessageResponse().getResponse().getResponseInfo().getStatus().getCode())) {
                return ResponseEntity.ok(resultString);
            } else {
                return ResponseEntity.internalServerError().body(resultString);
            }
        } catch (NullPointerException e) {
            return ResponseEntity.internalServerError().body(resultString);
        }
    }
}