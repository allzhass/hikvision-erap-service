package kz.bdl.erapservice.repository;

import kz.bdl.erapservice.entity.Auto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoRepository extends JpaRepository<Auto, Long> {
    List<Auto> findByPlateNumber(String plateNumber);
}