package pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.entity.AcademicCalendarEntity;
import reactor.core.publisher.Flux;

@Repository
public interface R2dbcCalendarRepository extends ReactiveCrudRepository<AcademicCalendarEntity, Integer> {
    
    Flux<AcademicCalendarEntity> findByInstitutionId(String institutionId);
}
