package pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.sigei.civicDates.domain.model.AcademicCalendar;
import pe.edu.vallegrande.sigei.civicDates.domain.model.EventCalendar;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.entity.AcademicCalendarEntity;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.entity.EventCalendarEntity;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.mapper.CalendarPersistenceMapper;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.repository.R2dbcCalendarRepository;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.repository.R2dbcEventCalendarRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarPersistenceAdapterTest {

    @Mock
    private R2dbcCalendarRepository r2dbcCalendarRepository;

    @Mock
    private R2dbcEventCalendarRepository r2dbcEventCalendarRepository;

    @Mock
    private CalendarPersistenceMapper mapper;

    @InjectMocks
    private CalendarPersistenceAdapter adapter;

    @Test
    void calendarMethods_shouldMapAndDelegate() {
        AcademicCalendar domain = AcademicCalendar.builder().id(1).institutionId("INST").build();
        AcademicCalendarEntity entity = AcademicCalendarEntity.builder().id(1).institutionId("INST").build();

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);
        when(r2dbcCalendarRepository.save(entity)).thenReturn(Mono.just(entity));
        when(r2dbcCalendarRepository.findById(1)).thenReturn(Mono.just(entity));
        when(r2dbcCalendarRepository.findAll()).thenReturn(Flux.just(entity));
        when(r2dbcCalendarRepository.findByInstitutionId("INST")).thenReturn(Flux.just(entity));

        StepVerifier.create(adapter.save(domain)).expectNext(domain).verifyComplete();
        StepVerifier.create(adapter.update(domain)).expectNext(domain).verifyComplete();
        StepVerifier.create(adapter.findById(1)).expectNext(domain).verifyComplete();
        StepVerifier.create(adapter.findAll()).expectNext(domain).verifyComplete();
        StepVerifier.create(adapter.findByInstitutionId("INST")).expectNext(domain).verifyComplete();
    }

    @Test
    void eventCalendarMethods_shouldMapAndDelegate() {
        EventCalendar domain = EventCalendar.builder().id(7).calendarId(1).eventId(10L).build();
        EventCalendarEntity entity = EventCalendarEntity.builder().id(7).calendarId(1).eventId(10L).build();

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);
        when(r2dbcEventCalendarRepository.save(entity)).thenReturn(Mono.just(entity));
        when(r2dbcEventCalendarRepository.findByCalendarId(1)).thenReturn(Flux.just(entity));
        when(r2dbcEventCalendarRepository.findByEventId(10L)).thenReturn(Flux.just(entity));
        when(r2dbcEventCalendarRepository.deleteByCalendarIdAndEventId(1, 10L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.save(domain)).expectNext(domain).verifyComplete();
        StepVerifier.create(adapter.findByCalendarId(1)).expectNext(domain).verifyComplete();
        StepVerifier.create(adapter.findByEventId(10L)).expectNext(domain).verifyComplete();
        StepVerifier.create(adapter.deleteByCalendarIdAndEventId(1, 10L)).verifyComplete();
    }
}
