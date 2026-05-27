package pe.edu.vallegrande.sigei.civicDates.domain.port.in;

import pe.edu.vallegrande.sigei.civicDates.domain.model.AcademicCalendar;
import pe.edu.vallegrande.sigei.civicDates.domain.model.Event;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ManageCalendarUseCase {
    Mono<AcademicCalendar> createCalendar(AcademicCalendar calendar);
    Mono<AcademicCalendar> getCalendarById(Integer id);
    Flux<AcademicCalendar> getAllCalendars();
    Flux<AcademicCalendar> getCalendarsByInstitution(String institutionId);
    Flux<Event> getEventsByCalendar(Integer calendarId);
    Mono<Void> addEventsToCalendar(Integer calendarId, List<Long> eventIds);
    Mono<AcademicCalendar> importCalendar(AcademicCalendar calendar, List<Long> eventIds);
}
