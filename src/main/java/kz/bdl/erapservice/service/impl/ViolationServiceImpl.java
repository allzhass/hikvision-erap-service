package kz.bdl.erapservice.service.impl;

import feign.FeignException;
import kz.bdl.erapservice.dto.Constants;
import kz.bdl.erapservice.dto.erap.ErapViolation;
import kz.bdl.erapservice.entity.*;
import kz.bdl.erapservice.exception.ResourceBadRequestException;
import kz.bdl.erapservice.exception.ResourceInternalException;
import kz.bdl.erapservice.exception.ResourceSuccessException;
import kz.bdl.erapservice.external.XmlApacheHttpService;
import kz.bdl.erapservice.mapper.ViolationMapper;
import kz.bdl.erapservice.mapper.VshepMapper;
import kz.bdl.erapservice.service.BDLService;
import kz.bdl.erapservice.service.SignService;
import kz.bdl.erapservice.service.ViolationService;
import kz.bdl.erapservice.signature.secretstorage.BundleProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ViolationServiceImpl implements ViolationService {
    private BDLService bdlService;
    private SignService signService;
    private XmlApacheHttpService xmlApacheHttpService;
    private final BundleProvider bundleProvider;

    @Override
    public CameraViolation checkViolation(Camera camera, String violationCode, SentViolations sentViolations) {
        log.info("Check Violation: MessageId={}; PlateNumber={}", sentViolations.getMessageId(), sentViolations.getPlateNumber());

        Violation violation;
        if (Constants.BRAND_MERGEN.equals(camera.getBrand()) || Constants.BRAND_HIK.equals(camera.getBrand())) {
            violation = bdlService.getViolationByHikCode(violationCode);
        } else if (Constants.BRAND_OPER.equals(camera.getBrand())) {
            violation = bdlService.getViolationByOperCode(violationCode);
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
        List<SentViolations> sentViolationsList = bdlService.getSentViolationsByDeviceNumberAndPlateNumberAndMessageId(
                sentViolations.getCameraViolation().getCamera().getApk().getDeviceNumber(),
                sentViolations.getPlateNumber(),
                sentViolations.getMessageId()
        );
        if (sentViolationsList.size() > 0) {
            if (sentViolationsList.get(0).getIsError()) {
                sentViolations.setId(sentViolationsList.get(0).getId());
            } else {
                throw new ResourceSuccessException(String.format(
                        "There is already exist success row: deviceNumber: %s; plateNumber: %s; messageId: %s",
                        sentViolationsList.get(0).getCameraViolation().getCamera().getApk().getDeviceNumber(),
                        sentViolationsList.get(0).getPlateNumber(),
                        sentViolationsList.get(0).getMessageId()));
            }
        }
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
        log.info("Sent Violation: MessageId={}; PlateNumber={}", sentViolations.getMessageId(), sentViolations.getPlateNumber());

        String violationXml = ViolationMapper.toXmlString(erapViolation);
        
        // Get certificate ID from VshepData
        Long certId = sentViolations.getCameraViolation().getCamera().getApk()
                .getLocation().getRegion().getVshepData().getCertId();
        
        String signedViolationXml = signService.signXml(violationXml, bundleProvider.getSignBundle(certId));

        String sendMessageXml = VshepMapper.wrapViolation(signedViolationXml);
        String soapString = signService.signSoap(sendMessageXml, bundleProvider.getSignBundle(certId));

        log.info("Set request to sentViolationEntiry: MessageId={}; PlateNumber={}", sentViolations.getMessageId(), sentViolations.getPlateNumber());
        sentViolations.setRequest(soapString);
        log.info("Set request to sentViolationEntiry: MessageId={}; PlateNumber={}", sentViolations.getMessageId(), sentViolations.getPlateNumber());

        String vshepResult;
        try {
            VshepData vshepData = sentViolations.getCameraViolation().getCamera().getApk()
                    .getLocation().getRegion().getVshepData();
            String url = (sentViolations.getCameraViolation().getIsProd()) ? vshepData.getURL() : vshepData.getTestUrl();

            log.info("Sending violation to SmartBridge: URL={}; MessageId={}; PlateNumber={}",
                    url, sentViolations.getMessageId(), sentViolations.getPlateNumber());

            vshepResult = xmlApacheHttpService.sendXmlRequest(url, soapString);

        } catch (InterruptedException | IOException e) {
            log.error("Error while sending violation to SmartBridge: {}! MessageId={}; PlateNumber={}",
                    e.getMessage(), sentViolations.getMessageId(), sentViolations.getPlateNumber());
            e.printStackTrace();
            sentViolations.setResponse(e.getMessage());
            throw new ResourceInternalException(String.format("Internal error: %s", e.getMessage()));
        } catch (FeignException e) {
            log.error("Error while sending violation to SmartBridge: {}! MessageId={}; PlateNumber={}; Response Body: {}",
                    e.getMessage(), sentViolations.getMessageId(), sentViolations.getPlateNumber(), e.responseBody());
            e.printStackTrace();
            vshepResult = e.responseBody()
                    .map(byteBuffer -> StandardCharsets.UTF_8.decode(byteBuffer).toString())
                    .orElse("No body");

            sentViolations.setResponse(vshepResult);
            throw new ResourceInternalException(String.format("Internal error: %s", e.getMessage()));
        }

        sentViolations.setResponse(vshepResult);
        log.info("Sending violation result: MessageId={}; PlateNumber={}; Response Body: {}",
                sentViolations.getMessageId(), sentViolations.getPlateNumber(), vshepResult);
        return vshepResult;
    }
}
