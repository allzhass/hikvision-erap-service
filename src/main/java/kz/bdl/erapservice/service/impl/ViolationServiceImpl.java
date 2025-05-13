package kz.bdl.erapservice.service.impl;

import feign.FeignException;
import kz.bdl.erapservice.dto.Constants;
import kz.bdl.erapservice.dto.erap.ErapViolation;
import kz.bdl.erapservice.entity.Camera;
import kz.bdl.erapservice.entity.CameraViolation;
import kz.bdl.erapservice.entity.SentViolations;
import kz.bdl.erapservice.entity.Violation;
import kz.bdl.erapservice.exception.ResourceBadRequestException;
import kz.bdl.erapservice.external.SmartBridgeServiceClient;
import kz.bdl.erapservice.mapper.ViolationMapper;
import kz.bdl.erapservice.mapper.VshepMapper;
import kz.bdl.erapservice.service.BDLService;
import kz.bdl.erapservice.service.SignService;
import kz.bdl.erapservice.service.ViolationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@AllArgsConstructor
public class ViolationServiceImpl implements ViolationService {
    private BDLService bdlService;
    private SignService signService;
    private SmartBridgeServiceClient smartBridgeServiceClient;

    @Override
    public CameraViolation checkViolation(Camera camera, String violationCode, SentViolations sentViolations) {
        Violation violation;
        if (Constants.BRAND_OPER.equals(camera.getBrand())) {
            violation = bdlService.getViolationByOperCode(violationCode);
        } else if (Constants.BRAND_HIK.equals(camera.getBrand())) {
            violation = bdlService.getViolationByHikCode(violationCode);
        } else {
            throw new ResourceBadRequestException(String.format(
                    "Unknown camera brand: id: %s; code: %s; brand: %s",
                    camera.getId(),
                    camera.getCode(),
                    camera.getBrand()));
        }

        CameraViolation cameraViolation = bdlService.getCameraViolationByCameraAndViolation(camera, violation);
        if (cameraViolation == null || !cameraViolation.getIsSendErap()) {
            throw new ResourceBadRequestException(String.format(
                    "Camera is not configured to send this violation: camera: %s; violation: %s",
                    camera.getCode(),
                    violation.getCode()));
        }

        sentViolations.setCameraViolation(cameraViolation);
        return cameraViolation;
    }

    @Override
    public CameraViolation checkViolationByCode(String cameraCode, String violationCode, SentViolations sentViolations) {
        Camera camera = bdlService.getCameraByCode(cameraCode);
        if (camera == null) {
            throw new ResourceBadRequestException(String.format(
                    "Unknown camera: code: %s", cameraCode));
        }

        return checkViolation(camera, violationCode, sentViolations);
    }

    @Override
    public String sendViolation(ErapViolation erapViolation, SentViolations sentViolations) {
        String violationXml = ViolationMapper.toXmlString(erapViolation);
        String signedViolationXml = signService.signXml(violationXml);

        String sendMessageXml = VshepMapper.wrapViolation(signedViolationXml);
        String soapString = signService.signSoap(sendMessageXml);
        sentViolations.setRequest(soapString);

        log.info("Sending violation: {}", soapString);
        String vshepResult;
        try {
            vshepResult = smartBridgeServiceClient.sendMessage(soapString);
        } catch (FeignException e) {
            log.error("Error while sending violation to SmartBridge: {}. Response Body: {}", e.getMessage(), e.responseBody());
            e.printStackTrace();
            vshepResult = e.responseBody()
                    .map(byteBuffer -> StandardCharsets.UTF_8.decode(byteBuffer).toString())
                    .orElse("No body");;
        }

        log.info("Sending violation resp: {}", vshepResult);

        sentViolations.setResponse(vshepResult);
        log.info("Saving request: {}", sentViolations.getRequest());
        log.info("Saving response: {}", sentViolations.getResponse());

        return vshepResult;
    }
}
