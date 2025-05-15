package kz.bdl.erapservice.repository;

import kz.bdl.erapservice.entity.SentViolations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentViolationsRepository extends JpaRepository<SentViolations, Long> {

    @Query("SELECT s FROM SentViolations s WHERE s.cameraViolation.camera.apk.deviceNumber = :deviceNumber " +
            "AND s.plateNumber = :plateNumber AND s.messageId = :messageId")
    List<SentViolations> findByDeviceNumberAndPlateNumberAndMessageId(
            @Param("deviceNumber") String deviceNumber,
            @Param("plateNumber") String plateNumber,
            @Param("messageId") String messageId);
}
