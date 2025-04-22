package kz.bdl.erapservice.service;

import kz.bdl.erapservice.entity.Camera;
import kz.bdl.erapservice.entity.CameraViolation;
import kz.bdl.erapservice.entity.SentViolations;
import kz.bdl.erapservice.entity.Violation;
import org.springframework.http.ResponseEntity;

public interface BDLService {
    Camera getCameraByCode(String code);
    Violation getViolationByOperCode(String operCode);
    Violation getViolationByHikCode(String hikCode);
    CameraViolation getCameraViolationByCameraAndViolation(Camera camera, Violation violation);
    ResponseEntity<String> addSentViolation(SentViolations sentViolations);
}
