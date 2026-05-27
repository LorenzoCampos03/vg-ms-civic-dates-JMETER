package pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.entity.EventCalendarEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface R2dbcEventCalendarRepository extends ReactiveCrudRepository<EventCalendarEntity, Integer> {
    
    Flux<EventCalendarEntity> findByCalendarId(Integer calendarId);
    
    Flux<EventCalendarEntity> findByEventId(Long eventId);
    
    @Query("DELETE FROM event_calendar WHERE calendar_id = :calendarId AND event_id = :eventId")
    Mono<Void> deleteByCalendarIdAndEventId(Integer calendarId, Long eventId);
}
