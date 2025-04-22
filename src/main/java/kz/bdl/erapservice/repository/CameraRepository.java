package kz.bdl.erapservice.repository;

import kz.bdl.erapservice.entity.Camera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Long> {
    List<Camera> findByCode(String code);
    List<Camera> findByIp(String ip);
    List<Camera> findByName(String name);
}
