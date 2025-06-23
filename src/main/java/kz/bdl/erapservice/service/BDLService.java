package kz.bdl.erapservice.service;

import kz.bdl.erapservice.entity.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BDLService {
    APK getApkByDeviceNumber(String deviceNumber);
    List<Camera> getCamerasByApk(APK apk);
    Camera getCameraByCode(String code);
    Violation getViolationByOperCode(String operCode);
    Violation getViolationByHikCode(String hikCode);
    CameraViolation getCameraViolationByCameraAndViolation(Camera camera, Violation violation);
    List<SentViolations> getSentViolationsByDeviceNumberAndPlateNumberAndMessageId(
            String deviceNumber, String plateNumber, String messageId);
    List<SentViolations> getSentViolationsByMessageId(String messageId);

    ResponseEntity<String> updateSentViolation(SentViolations sentViolations);
}
