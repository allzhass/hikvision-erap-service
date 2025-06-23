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
import kz.bdl.erapservice.exception.ResourceSuccessException;
import kz.bdl.erapservice.external.FileDownloadWebClient;
import kz.bdl.erapservice.mapper.ViolationMapper;
import kz.bdl.erapservice.mapper.VshepMapper;
import kz.bdl.erapservice.service.AutoService;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    private AutoService autoService;
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
            } catch (IOException e) {
                e.printStackTrace();
                throw new ResourceBadRequestException(String.format(
                        "Error of reading request body: %s", e.getMessage()));
            }

            Envelope envelope;
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(Envelope.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                envelope = (Envelope) unmarshaller.unmarshal(new StringReader(rawBody));
            } catch (Exception e) {
                sentViolations.setRequest(rawBody);
                e.printStackTrace();
                throw new ResourceBadRequestException(String.format(
                        "Error of unmarshalling request body: %s", e.getMessage()));
            }

            ErapViolation erapViolation = envelope.getBody().getSendMessage().getRequest().getRequestData().getData().getOnEventShep().getViolation();
            sentViolations.setMessageId(erapViolation.getMessageId());
            sentViolations.setPlateNumber(erapViolation.getPlateNumber());

            APK apk = bdlService.getApkByDeviceNumber(erapViolation.getDeviceNumber());
            if (apk == null) {
                throw new ResourceSuccessException(String.format(
                        "Unknown APK: number: %s", erapViolation.getDeviceNumber()));
            }

            List<Camera> cameras = bdlService.getCamerasByApk(apk);
            Camera camera = getCamera(cameras, erapViolation);
            violationService.checkViolation(camera, erapViolation.getViolationCode(), sentViolations);
            erapViolation.enrich(apk);

            LocalDateTime givenTime = LocalDateTime.parse(erapViolation.getSendAt());
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime threshold = now.minusHours(3);
            if (givenTime.isBefore(threshold)) {
                String errMsg = String.format("This violation received too later: PlateNumber=%s; SendAt:%s", erapViolation.getPlateNumber(), erapViolation.getSendAt());
                log.error(errMsg);
                throw new ResourceSuccessException(errMsg);
            }

            if (!autoService.isSendAutoViolation(erapViolation.getPlateNumber())) {
                String errMsg = String.format("Violations of this auto cannot be send to ERAP: %s", erapViolation.getPlateNumber());
                log.error(errMsg);
                throw new ResourceSuccessException(errMsg);
            }

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
                    sentViolations.setIsError(false);
                    return ResponseEntity.ok(resultString);
                } else if (resultString.contains("duplicate key value violates unique constraint \"message_client_guid_message_id_key\"")) {
                    throw new ResourceSuccessException(String.format("ResourceSuccessException: %s", resultString));
                } else {
                    throw new ResourceInternalException(String.format("Internal error: %s", resultString));
                }
            } catch (NullPointerException e) {
                throw new ResourceInternalException(String.format("Internal error: %s", resultString));
            }
        } catch (ResourceSuccessException e) {
            sentViolations.setResponse(e.getMessage());
            sentViolations.setIsError(true);
            log.error("ResourceSuccessException: MessageId={}; PlateNumber={}; Error={}",
                    sentViolations.getMessageId(), sentViolations.getPlateNumber(), e.getMessage());

            String resultString = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Header><wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" soap:mustUnderstand=\"1\"><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:SignedInfo><ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"></ds:CanonicalizationMethod><ds:SignatureMethod Algorithm=\"urn:ietf:params:xml:ns:pkigovkz:xmlsec:algorithms:gostr34102015-gostr34112015-512\"></ds:SignatureMethod><ds:Reference URI=\"#id-1cd6f524-401c-4366-abd5-e29dde58ec38\"><ds:Transforms><ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"></ds:Transform></ds:Transforms><ds:DigestMethod Algorithm=\"urn:ietf:params:xml:ns:pkigovkz:xmlsec:algorithms:gostr34112015-512\"></ds:DigestMethod><ds:DigestValue>uHAPf6GAaHMHhu6BPiPBkqhve1dylFB7p4hDzgJMOKHLczVvmJwfiapOQIBnbJhxfGtZMr3oFK8kj38vjifaAA==</ds:DigestValue></ds:Reference></ds:SignedInfo><ds:SignatureValue>9y4anHAqKp7gRxl5UrhmHHJ4M8XbaSTDEWvWiS1O8M5enlk+R29SpQvb70MauuFFuY5OdxaTwoF1+VUMDagcg16rGKCU6u7F1uodP7zKQc3cK02KTaIHwTq+FIiy7UPiB8xqEkpnesGz8SJcRRmsc02Q/WcuZA0JaLIoaLafR20=</ds:SignatureValue><ds:KeyInfo><wsse:SecurityTokenReference><ds:X509Data><ds:X509IssuerSerial><ds:X509IssuerName>C=KZ,CN=ҰЛТТЫҚ КУӘЛАНДЫРУШЫ ОРТАЛЫҚ (GOST) 2022</ds:X509IssuerName><ds:X509SerialNumber>431184608699948652601025965397585293475933839593</ds:X509SerialNumber></ds:X509IssuerSerial></ds:X509Data></wsse:SecurityTokenReference></ds:KeyInfo></ds:Signature></wsse:Security></soap:Header><soap:Body xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" wsu:Id=\"id-1cd6f524-401c-4366-abd5-e29dde58ec38\"><ns2:SendMessageResponse xmlns:ns2=\"http://bip.bee.kz/SyncChannel/v10/Types\"><response><responseInfo><messageId>9d1c938b-f840-4cf7-8b56-1e1be4a9f7b7</messageId><responseDate>2025-06-23T12:40:34.477+05:00</responseDate><status><code>SCSS001</code><message>Message has been processed successfully</message></status></responseInfo><responseData><data><return>true</return></data></responseData></response></ns2:SendMessageResponse></soap:Body></soap:Envelope>";
            return ResponseEntity.ok(resultString);

        } catch (RuntimeException e) {
            sentViolations.setResponse(e.getMessage());
            sentViolations.setIsError(true);
            log.error("RuntimeException: MessageId={}; PlateNumber={}; Error={}",
                    sentViolations.getMessageId(), sentViolations.getPlateNumber(), e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().contentType(MediaType.TEXT_PLAIN).body(e.getMessage());

        } finally {
            ZonedDateTime gmtZonedDateTime = ZonedDateTime.now(ZoneId.of("GMT"));
            sentViolations.setCreatedAt(gmtZonedDateTime.toLocalDateTime());
            log.info("Saving request: SentViolations={}", sentViolations);
            bdlService.updateSentViolation(sentViolations);
        }
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