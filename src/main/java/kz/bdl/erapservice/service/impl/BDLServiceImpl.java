package kz.bdl.erapservice.service.impl;

import kz.bdl.erapservice.entity.*;
import kz.bdl.erapservice.repository.*;
import kz.bdl.erapservice.service.BDLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BDLServiceImpl implements BDLService {
    @Autowired
    private CameraRepository cameraRepository;
    @Autowired
    private APKRepository apkRepository;
    @Autowired
    private ViolationRepository violationRepository;
    @Autowired
    private CameraViolationRepository cameraViolationRepository;
    @Autowired
    private SentViolationsRepository sentViolationsRepository;

    @Override
    public Camera getCameraByCode(String code) {
        List<Camera> cameras = cameraRepository.findByCode(code);
        if (cameras.size() > 0) {
            return cameras.get(0);
        }

        cameras = cameraRepository.findByIp(code);
        if (cameras.size() > 0) {
            return cameras.get(0);
        }

        cameras = cameraRepository.findByName(code);
        if (cameras.size() > 0) {
            return cameras.get(0);
        }

        return null;
    }

    @Override
    public APK getApkByDeviceNumber(String deviceNumber) {
        List<APK> apk = apkRepository.getAPKByDeviceNumber(deviceNumber);
        if (apk.size() > 0) {
            return apk.get(0);
        }

        return null;
    }

    @Override
    public List<Camera> getCamerasByApk(APK apk) {
        List<Camera> cameras = cameraRepository.findByApk(apk);
        if (cameras.size() > 0) {
            return cameras;
        }

        return null;
    }

    @Override
    public Violation getViolationByOperCode(String operCode) {
        return violationRepository.getViolationByOperCode(operCode);
    }

    @Override
    public Violation getViolationByHikCode(String hikCode) {
        return violationRepository.getViolationByHikCode(hikCode);
    }

    @Override
    public CameraViolation getCameraViolationByCameraAndViolation(Camera camera, Violation violation) {
        return cameraViolationRepository.getCameraViolationByCameraAndViolation(camera, violation);
    }

    @Override
    @Transactional
    public List<SentViolations> getSentViolationsByDeviceNumberAndPlateNumberAndMessageId(
            String deviceNumber, String plateNumber, String messageId) {
        return sentViolationsRepository.findByDeviceNumberAndPlateNumberAndMessageId(
                deviceNumber, plateNumber, messageId);
    }

    @Override
    @Transactional
    public List<SentViolations> getSentViolationsByMessageId(String messageId) {
        return sentViolationsRepository.findByMessageId(messageId);
    }

    @Override
    @Transactional
    public ResponseEntity<String> updateSentViolation(SentViolations sentViolations) {
        sentViolationsRepository.save(sentViolations);
        return ResponseEntity.ok("Sent Violation updated successfully");
    }
}
