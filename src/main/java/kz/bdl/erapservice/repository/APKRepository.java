package kz.bdl.erapservice.repository;

import kz.bdl.erapservice.entity.APK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface APKRepository extends JpaRepository<APK, Long> {
    List<APK> getAPKByDeviceNumber(String deviceNumber);
}
