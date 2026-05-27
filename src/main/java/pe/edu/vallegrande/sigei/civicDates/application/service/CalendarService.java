package pe.edu.vallegrande.sigei.civicDates.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.sigei.civicDates.domain.exception.CalendarNotFoundException;
import pe.edu.vallegrande.sigei.civicDates.domain.exception.InstitutionMismatchException;
import pe.edu.vallegrande.sigei.civicDates.domain.model.AcademicCalendar;
import pe.edu.vallegrande.sigei.civicDates.domain.model.Event;
import pe.edu.vallegrande.sigei.civicDates.domain.model.EventCalendar;
import pe.edu.vallegrande.sigei.civicDates.domain.model.enums.CalendarStatus;
import pe.edu.vallegrande.sigei.civicDates.domain.port.in.ManageCalendarUseCase;
import pe.edu.vallegrande.sigei.civicDates.domain.port.out.AcademicCalendarRepository;
import pe.edu.vallegrande.sigei.civicDates.domain.port.out.EventCalendarRepository;
import pe.edu.vallegrande.sigei.civicDates.domain.port.out.EventRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.transaction.reactive.TransactionalOperator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService implements ManageCalendarUseCase {

    private final AcademicCalendarRepository calendarRepository;
    private final EventCalendarRepository eventCalendarRepository;
    private final EventRepository eventRepository;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<AcademicCalendar> createCalendar(AcademicCalendar calendar) {
        calendar.setCreatedAt(LocalDateTime.now());
        calendar.setUpdatedAt(LocalDateTime.now());
        if (calendar.getStatus() == null) {
            calendar.setStatus(CalendarStatus.ACTIVE);
        }
        return calendarRepository.save(calendar);
    }

    @Override
    public Mono<AcademicCalendar> getCalendarById(Integer id) {
        return calendarRepository.findById(id)
                .filter(cal -> cal.getStatus() == CalendarStatus.ACTIVE)
                .switchIfEmpty(Mono.error(new CalendarNotFoundException(id)));
    }

    @Override
    public Flux<AcademicCalendar> getAllCalendars() {
        return calendarRepository.findAll()
                .filter(cal -> cal.getStatus() == CalendarStatus.ACTIVE);
    }

    @Override
    public Flux<AcademicCalendar> getCalendarsByInstitution(String institutionId) {
        return calendarRepository.findByInstitutionId(institutionId)
                .filter(cal -> cal.getStatus() == CalendarStatus.ACTIVE);
    }

    @Override
    public Flux<Event> getEventsByCalendar(Integer calendarId) {
        return eventCalendarRepository.findByCalendarId(calendarId)
                .flatMap(eventCalendar -> eventRepository.findById(eventCalendar.getEventId()));
    }

    @Override
    public Mono<Void> addEventsToCalendar(Integer calendarId, List<Long> eventIds) {
        return addEventsToCalendarInternal(calendarId, eventIds)
            .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<AcademicCalendar> importCalendar(AcademicCalendar calendar, List<Long> eventIds) {
        return createCalendar(calendar)
            .flatMap(savedCalendar ->
                addEventsToCalendarInternal(savedCalendar.getId(), eventIds)
                    .thenReturn(savedCalendar)
            )
            .as(transactionalOperator::transactional);
        }

        private Mono<Void> addEventsToCalendarInternal(Integer calendarId, List<Long> eventIds) {
        List<Long> safeEventIds = eventIds == null ? List.of() : eventIds;

        return calendarRepository.findById(calendarId)
            .switchIfEmpty(Mono.error(new CalendarNotFoundException(calendarId)))
            .flatMap(calendar ->
                eventCalendarRepository.findByCalendarId(calendarId)
                    .map(EventCalendar::getEventId)
                    .collectList()
                    .flatMapMany(existingEventIds ->
                        Flux.fromIterable(safeEventIds)
                            .filter(eventId -> !existingEventIds.contains(eventId))
                    )
                    .concatMap(eventId ->
                        eventRepository.findById(eventId)
                            .flatMap(event -> {
                                if (!event.getIsRecurring() &&
                                    !event.getInstitutionId().equals(calendar.getInstitutionId())) {
                                return Mono.error(new InstitutionMismatchException(
                                    eventId,
                                    event.getInstitutionId(),
                                    calendar.getInstitutionId()
                                ));
                                }

                                EventCalendar eventCalendar = EventCalendar.builder()
                                    .calendarId(calendarId)
                                    .eventId(eventId)
                                    .createdAt(LocalDateTime.now())
                                    .build();
                                return eventCalendarRepository.save(eventCalendar);
                            })
                    )
                    .then()
            );
    }

    public Mono<AcademicCalendar> updateCalendar(Integer id, AcademicCalendar calendarUpdate) {
        return calendarRepository.findById(id)
                .filter(cal -> cal.getStatus() == CalendarStatus.ACTIVE)
                .switchIfEmpty(Mono.error(new CalendarNotFoundException(id)))
                .flatMap(existingCalendar -> {
                    if (calendarUpdate.getAcademicYear() != null) {
                        existingCalendar.setAcademicYear(calendarUpdate.getAcademicYear());
                    }
                    if (calendarUpdate.getAcademicYearName() != null) {
                        existingCalendar.setAcademicYearName(calendarUpdate.getAcademicYearName());
                    }
                    if (calendarUpdate.getStartDate() != null) {
                        existingCalendar.setStartDate(calendarUpdate.getStartDate());
                    }
                    if (calendarUpdate.getEndDate() != null) {
                        existingCalendar.setEndDate(calendarUpdate.getEndDate());
                    }
                    existingCalendar.setUpdatedAt(LocalDateTime.now());
                    return calendarRepository.save(existingCalendar);
                });
    }

    public Mono<Void> deleteCalendarLogically(Integer id) {
        return calendarRepository.findById(id)
                .filter(cal -> cal.getStatus() == CalendarStatus.ACTIVE)
                .switchIfEmpty(Mono.error(new CalendarNotFoundException(id)))
                .flatMap(calendar -> {
                    calendar.setStatus(CalendarStatus.INACTIVE);
                    calendar.setUpdatedAt(LocalDateTime.now());
                    return calendarRepository.save(calendar).then();
                })
                .as(transactionalOperator::transactional);
    }

    public Mono<AcademicCalendar> reactivateCalendar(Integer id) {
        return calendarRepository.findById(id)
                .switchIfEmpty(Mono.error(new CalendarNotFoundException(id)))
                .filter(cal -> cal.getStatus() == CalendarStatus.INACTIVE)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Calendar is not inactive or does not exist")))
                .flatMap(calendar -> {
                    calendar.setStatus(CalendarStatus.ACTIVE);
                    calendar.setUpdatedAt(LocalDateTime.now());
                    return calendarRepository.save(calendar);
                })
                .as(transactionalOperator::transactional);
    }
}
