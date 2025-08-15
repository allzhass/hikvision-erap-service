package kz.bdl.erapservice.repository;

import kz.bdl.erapservice.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Event findByExternalId(String externalId);
}
