package pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.entity.EventTypeImageEntity;

@Repository
public interface R2dbcEventTypeImageRepository extends ReactiveCrudRepository<EventTypeImageEntity, String> {
}
