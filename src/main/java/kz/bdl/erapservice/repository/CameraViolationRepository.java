package kz.bdl.erapservice.repository;

import kz.bdl.erapservice.entity.Camera;
import kz.bdl.erapservice.entity.CameraViolation;
import kz.bdl.erapservice.entity.Violation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CameraViolationRepository extends JpaRepository<CameraViolation, Long> {
    CameraViolation getCameraViolationByCameraAndViolation(Camera camera, Violation violation);
}