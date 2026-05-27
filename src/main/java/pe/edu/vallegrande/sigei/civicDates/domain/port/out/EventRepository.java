package pe.edu.vallegrande.sigei.civicDates.domain.port.out;

import pe.edu.vallegrande.sigei.civicDates.domain.model.Event;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventRepository {
    Mono<Event> save(Event event);
    Mono<Event> findById(Long id);
    Flux<Event> findAll();
    Flux<Event> findByInstitutionId(String institutionId);
    Flux<Event> findByStatus(String status);
    Mono<Void> deleteById(Long id);
    Mono<Event> update(Event event);
}
