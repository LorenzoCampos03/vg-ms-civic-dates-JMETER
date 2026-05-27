package pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.vallegrande.sigei.civicDates.domain.model.AcademicCalendar;
import pe.edu.vallegrande.sigei.civicDates.domain.model.EventCalendar;
import pe.edu.vallegrande.sigei.civicDates.domain.port.out.AcademicCalendarRepository;
import pe.edu.vallegrande.sigei.civicDates.domain.port.out.EventCalendarRepository;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.mapper.CalendarPersistenceMapper;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.repository.R2dbcCalendarRepository;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.repository.R2dbcEventCalendarRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CalendarPersistenceAdapter implements AcademicCalendarRepository, EventCalendarRepository {

    private final R2dbcCalendarRepository r2dbcCalendarRepository;
    private final R2dbcEventCalendarRepository r2dbcEventCalendarRepository;
    private final CalendarPersistenceMapper mapper;

    // AcademicCalendarRepository methods
    @Override
    public Mono<AcademicCalendar> save(AcademicCalendar calendar) {
        return r2dbcCalendarRepository.save(mapper.toEntity(calendar))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<AcademicCalendar> findById(Integer id) {
        return r2dbcCalendarRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<AcademicCalendar> findAll() {
        return r2dbcCalendarRepository.findAll()
                .map(mapper::toDomain);
    }

    @Override
    public Flux<AcademicCalendar> findByInstitutionId(String institutionId) {
        return r2dbcCalendarRepository.findByInstitutionId(institutionId)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<AcademicCalendar> update(AcademicCalendar calendar) {
        return r2dbcCalendarRepository.save(mapper.toEntity(calendar))
                .map(mapper::toDomain);
    }

    // EventCalendarRepository methods
    @Override
    public Mono<EventCalendar> save(EventCalendar eventCalendar) {
        return r2dbcEventCalendarRepository.save(mapper.toEntity(eventCalendar))
                .map(mapper::toDomain);
    }

    @Override
    public Flux<EventCalendar> findByCalendarId(Integer calendarId) {
        return r2dbcEventCalendarRepository.findByCalendarId(calendarId)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<EventCalendar> findByEventId(Long eventId) {
        return r2dbcEventCalendarRepository.findByEventId(eventId)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteByCalendarIdAndEventId(Integer calendarId, Long eventId) {
        return r2dbcEventCalendarRepository.deleteByCalendarIdAndEventId(calendarId, eventId);
    }
}
