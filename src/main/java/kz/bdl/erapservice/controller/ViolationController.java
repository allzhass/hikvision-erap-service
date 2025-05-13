package kz.bdl.erapservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import kz.bdl.erapservice.dto.FrameData;
import kz.bdl.erapservice.dto.RequestDTO;
import kz.bdl.erapservice.dto.cerebra.CerebraRequestDTO;
import kz.bdl.erapservice.dto.erap.ErapViolation;
import kz.bdl.erapservice.dto.vshep.request.Envelope;
import kz.bdl.erapservice.dto.vshep.response.Response;
import kz.bdl.erapservice.dto.vshep.response.VshepStatus;
import kz.bdl.erapservice.entity.APK;
import kz.bdl.erapservice.entity.Camera;
import kz.bdl.erapservice.entity.CameraViolation;
import kz.bdl.erapservice.entity.SentViolations;
import kz.bdl.erapservice.exception.ResourceBadRequestException;
import kz.bdl.erapservice.exception.ResourceInternalException;
import kz.bdl.erapservice.external.FileDownloadWebClient;
import kz.bdl.erapservice.mapper.ViolationMapper;
import kz.bdl.erapservice.mapper.VshepMapper;
import kz.bdl.erapservice.service.BDLService;
import kz.bdl.erapservice.service.ViolationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/violation")
@Slf4j
public class ViolationController {
    private BDLService bdlService;
    private ViolationService violationService;
    private FileDownloadWebClient fileDownloadWebClient;

    @PostMapping
    public ResponseEntity<Response> basicViolation(
            @RequestBody RequestDTO requestDTO) {

        SentViolations sentViolations = new SentViolations();
        CameraViolation cameraViolation = violationService.checkViolationByCode(requestDTO.getCameraCode(), requestDTO.getViolationCode(), sentViolations);

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
        CameraViolation cameraViolation = violationService.checkViolationByCode(
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

    @RequestMapping(path = "/erap", method = RequestMethod.POST)
    public ResponseEntity<String> receiveSoapRequest(HttpServletRequest request) {
        SentViolations sentViolations = new SentViolations();
        try {
            log.info("Получен запрос на /erap с заголовками:");
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                log.info("{}: {}", headerName, headerValue);
            }

            String rawBody;
            try (BufferedReader reader = request.getReader()) {
                rawBody = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                sentViolations.setRequest(rawBody);
            } catch (IOException e) {
                log.error("Error of reading request body", e);
                throw new ResourceBadRequestException(String.format(
                        "Error of reading request body: %s", e.getMessage()));
            }

            Envelope envelope;
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(Envelope.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                envelope = (Envelope) unmarshaller.unmarshal(new StringReader(rawBody));
            } catch (Exception e) {
                e.printStackTrace();
                throw new ResourceBadRequestException(String.format(
                        "Error of unmarshalling request body: %s", e.getMessage()));
            }

            ErapViolation erapViolation = envelope.getBody().getSendMessage().getRequest().getRequestData().getData().getOnEventShep().getViolation();
            APK apk = bdlService.getApkByDeviceNumber(erapViolation.getDeviceNumber());
            if (apk == null) {
                log.error("Unknown APK: number: {}", erapViolation.getDeviceNumber());
                throw new ResourceBadRequestException(String.format(
                        "Unknown APK: number: %s", erapViolation.getDeviceNumber()));
            }

            List<Camera> cameras = bdlService.getCamerasByApk(apk);
            Camera camera = getCamera(cameras, erapViolation);

            violationService.checkViolation(camera, erapViolation.getViolationCode(), sentViolations);

            erapViolation.enrich(apk);
            String resultString = violationService.sendViolation(erapViolation, sentViolations);

            kz.bdl.erapservice.dto.vshep.response.Envelope envelopeResponse;
            try {
                envelopeResponse = VshepMapper.toEnvelope(resultString);
            } catch (JAXBException e) {
                e.printStackTrace();
                throw new ResourceInternalException(String.format("Internal error: %s", resultString));
            }

            try {
                if (VshepStatus.OK.equals(
                        envelopeResponse.getBody().getSendMessageResponse().getResponse().getResponseInfo().getStatus().getCode())) {
                    return ResponseEntity.ok(resultString);
                } else {
                    throw new ResourceInternalException(String.format("Internal error: %s", resultString));
                }
            } catch (NullPointerException e) {
                throw new ResourceInternalException(String.format("Internal error: %s", resultString));
            }
        } catch (RuntimeException e) {
            sentViolations.setResponse(e.getMessage());
        } finally {
            sentViolations.setCreatedAt(LocalDateTime.now());
            log.info("Saving request: {}", sentViolations);
            bdlService.addSentViolation(sentViolations);
        }
        return ResponseEntity.ok(sentViolations.getResponse());
    }

    @NotNull
    private static Camera getCamera(List<Camera> cameras, ErapViolation erapViolation) {
        for (Camera cameraItem : cameras) {
            log.info("Checking camera: {}", cameraItem);
            if (erapViolation.getLocationTitle().contains(cameraItem.getCode())
                || erapViolation.getLocationTitle().contains(cameraItem.getIp())
                || erapViolation.getLocationTitle().contains(cameraItem.getName())) {
                return cameraItem;
            }
        }
        if (cameras.size() > 0) {
            return cameras.get(0);
        } else {
            throw new ResourceBadRequestException(String.format(
                    "Unknown camera: Title: %s; DeviceNumber: %s", erapViolation.getLocationTitle(), erapViolation.getDeviceNumber()));
        }
    }
}