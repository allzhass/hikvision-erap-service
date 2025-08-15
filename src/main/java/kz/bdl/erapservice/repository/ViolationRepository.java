package kz.bdl.erapservice.repository;

import kz.bdl.erapservice.entity.Violation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViolationRepository extends JpaRepository<Violation, Long> {
    Violation getViolationByCode(String code);
    Violation getViolationByOperCode(String operCode);
    Violation getViolationByHikCode(String hikCode);
}
