package kz.bdl.erapservice.repository;

import kz.bdl.erapservice.entity.SentViolations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SentViolationsRepository extends JpaRepository<SentViolations, Long> {}
