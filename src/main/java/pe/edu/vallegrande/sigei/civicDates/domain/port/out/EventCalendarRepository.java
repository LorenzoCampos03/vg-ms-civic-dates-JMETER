package pe.edu.vallegrande.sigei.civicDates.domain.port.out;

import pe.edu.vallegrande.sigei.civicDates.domain.model.EventCalendar;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventCalendarRepository {
    Mono<EventCalendar> save(EventCalendar eventCalendar);
    Flux<EventCalendar> findByCalendarId(Integer calendarId);
    Flux<EventCalendar> findByEventId(Long eventId);
    Mono<Void> deleteByCalendarIdAndEventId(Integer calendarId, Long eventId);
}
