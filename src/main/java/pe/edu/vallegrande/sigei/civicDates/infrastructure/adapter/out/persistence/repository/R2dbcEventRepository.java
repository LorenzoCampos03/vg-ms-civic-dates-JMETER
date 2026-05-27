package pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.entity.EventEntity;
import reactor.core.publisher.Flux;

@Repository
public interface R2dbcEventRepository extends ReactiveCrudRepository<EventEntity, Long> {
    
    Flux<EventEntity> findByInstitutionId(String institutionId);
    
    @Query("SELECT * FROM event WHERE status = :status")
    Flux<EventEntity> findByStatus(String status);
}
