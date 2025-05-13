package kz.bdl.erapservice.service;

import kz.bdl.erapservice.dto.erap.ErapViolation;
import kz.bdl.erapservice.dto.vshep.response.Response;
import kz.bdl.erapservice.entity.Camera;
import kz.bdl.erapservice.entity.CameraViolation;
import kz.bdl.erapservice.entity.SentViolations;

public interface ViolationService {
    CameraViolation checkViolation(Camera camera, String violationCode, SentViolations sentViolations);
    CameraViolation checkViolationByCode(String cameraCode, String violationCode, SentViolations sentViolations);
    String sendViolation(ErapViolation erapViolation, SentViolations sentViolations);
}
