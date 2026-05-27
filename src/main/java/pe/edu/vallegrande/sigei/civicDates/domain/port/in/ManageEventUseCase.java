package pe.edu.vallegrande.sigei.civicDates.domain.port.in;

import pe.edu.vallegrande.sigei.civicDates.domain.model.Event;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManageEventUseCase {
    Mono<Event> createEvent(Event event);
    Mono<Event> updateEvent(Long id, Event event);
    Mono<Event> getEventById(Long id);
    Flux<Event> getAllEvents();
    Flux<Event> getEventsByInstitution(String institutionId);
    Flux<Event> getInactiveEvents();
    Mono<Void> deleteEvent(Long id);
    Mono<Event> restoreEvent(Long id);
}
