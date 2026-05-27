package pe.edu.vallegrande.sigei.civicDates.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import pe.edu.vallegrande.sigei.civicDates.domain.exception.CalendarNotFoundException;
import pe.edu.vallegrande.sigei.civicDates.domain.exception.InstitutionMismatchException;
import pe.edu.vallegrande.sigei.civicDates.domain.model.AcademicCalendar;
import pe.edu.vallegrande.sigei.civicDates.domain.model.Event;
import pe.edu.vallegrande.sigei.civicDates.domain.model.EventCalendar;
import pe.edu.vallegrande.sigei.civicDates.domain.model.enums.CalendarStatus;
import pe.edu.vallegrande.sigei.civicDates.domain.port.out.AcademicCalendarRepository;
import pe.edu.vallegrande.sigei.civicDates.domain.port.out.EventCalendarRepository;
import pe.edu.vallegrande.sigei.civicDates.domain.port.out.EventRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarServiceCoverageTest {

    @Mock
    private AcademicCalendarRepository calendarRepository;

    @Mock
    private EventCalendarRepository eventCalendarRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private TransactionalOperator transactionalOperator;

    @InjectMocks
    private CalendarService calendarService;

        @BeforeEach
        void setup() {
        }

        @SuppressWarnings("unchecked")
        private void stubTransactionalForMono() {
                when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));
        }

    @Test
    void createCalendar_shouldSetDefaultStatusAndTimestamps() {
        AcademicCalendar input = AcademicCalendar.builder()
                .institutionId("INST-1")
                .academicYear(2026)
                .build();

        when(calendarRepository.save(any(AcademicCalendar.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(calendarService.createCalendar(input))
                .assertNext(saved -> {
                    assertEquals(CalendarStatus.ACTIVE, saved.getStatus());
                    assertNotNull(saved.getCreatedAt());
                    assertNotNull(saved.getUpdatedAt());
                })
                .verifyComplete();
    }

    @Test
    void getCalendarById_shouldReturnErrorWhenInactive() {
        when(calendarRepository.findById(1)).thenReturn(Mono.just(AcademicCalendar.builder()
                .id(1)
                .status(CalendarStatus.INACTIVE)
                .build()));

        StepVerifier.create(calendarService.getCalendarById(1))
                .expectError(CalendarNotFoundException.class)
                .verify();
    }

    @Test
    void getEventsByCalendar_shouldLoadEventDetails() {
        EventCalendar relation = EventCalendar.builder().calendarId(1).eventId(10L).build();
        Event event = Event.builder().id(10L).title("Acto").build();

        when(eventCalendarRepository.findByCalendarId(1)).thenReturn(Flux.just(relation));
        when(eventRepository.findById(10L)).thenReturn(Mono.just(event));

        StepVerifier.create(calendarService.getEventsByCalendar(1))
                .assertNext(found -> assertEquals(10L, found.getId()))
                .verifyComplete();
    }

    @Test
    void addEventsToCalendar_shouldSkipExistingAndPersistOnlyNew() {
                stubTransactionalForMono();

        AcademicCalendar calendar = AcademicCalendar.builder().id(1).institutionId("INST-A").status(CalendarStatus.ACTIVE).build();
        EventCalendar existing = EventCalendar.builder().calendarId(1).eventId(1L).build();
        Event newEvent = Event.builder().id(2L).institutionId("INST-A").isRecurring(false).build();

        when(calendarRepository.findById(1)).thenReturn(Mono.just(calendar));
        when(eventCalendarRepository.findByCalendarId(1)).thenReturn(Flux.just(existing));
        when(eventRepository.findById(2L)).thenReturn(Mono.just(newEvent));
        when(eventCalendarRepository.save(any(EventCalendar.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(calendarService.addEventsToCalendar(1, List.of(1L, 2L)))
                .verifyComplete();

        ArgumentCaptor<EventCalendar> captor = ArgumentCaptor.forClass(EventCalendar.class);
        verify(eventCalendarRepository).save(captor.capture());
        assertEquals(2L, captor.getValue().getEventId());
    }

    @Test
    void addEventsToCalendar_shouldFailWhenInstitutionMismatchAndNotRecurring() {
                stubTransactionalForMono();

        AcademicCalendar calendar = AcademicCalendar.builder().id(1).institutionId("INST-A").status(CalendarStatus.ACTIVE).build();
        Event event = Event.builder().id(9L).institutionId("INST-B").isRecurring(false).build();

        when(calendarRepository.findById(1)).thenReturn(Mono.just(calendar));
        when(eventCalendarRepository.findByCalendarId(1)).thenReturn(Flux.empty());
        when(eventRepository.findById(9L)).thenReturn(Mono.just(event));

        StepVerifier.create(calendarService.addEventsToCalendar(1, List.of(9L)))
                .expectError(InstitutionMismatchException.class)
                .verify();

        verify(eventCalendarRepository, never()).save(any(EventCalendar.class));
    }

    @Test
    void importCalendar_shouldCreateAndReturnSavedCalendar() {
                stubTransactionalForMono();

        AcademicCalendar toCreate = AcademicCalendar.builder()
                .institutionId("INST-A")
                .academicYear(2027)
                .startDate(LocalDate.of(2027, 1, 1))
                .endDate(LocalDate.of(2027, 12, 31))
                .build();

        AcademicCalendar saved = AcademicCalendar.builder()
                .id(99)
                .institutionId("INST-A")
                .academicYear(2027)
                .status(CalendarStatus.ACTIVE)
                .build();

        when(calendarRepository.save(any(AcademicCalendar.class))).thenReturn(Mono.just(saved));
        when(calendarRepository.findById(99)).thenReturn(Mono.just(saved));
        when(eventCalendarRepository.findByCalendarId(99)).thenReturn(Flux.empty());

        StepVerifier.create(calendarService.importCalendar(toCreate, null))
                .assertNext(result -> assertEquals(99, result.getId()))
                .verifyComplete();
    }

    @Test
    void deleteCalendarLogically_shouldSetInactive() {
                stubTransactionalForMono();

        AcademicCalendar active = AcademicCalendar.builder().id(1).status(CalendarStatus.ACTIVE).build();
        when(calendarRepository.findById(1)).thenReturn(Mono.just(active));
        when(calendarRepository.save(any(AcademicCalendar.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(calendarService.deleteCalendarLogically(1))
                .verifyComplete();

        ArgumentCaptor<AcademicCalendar> captor = ArgumentCaptor.forClass(AcademicCalendar.class);
        verify(calendarRepository).save(captor.capture());
        assertEquals(CalendarStatus.INACTIVE, captor.getValue().getStatus());
    }

    @Test
    void reactivateCalendar_shouldReturnErrorWhenAlreadyActive() {
                stubTransactionalForMono();

        when(calendarRepository.findById(1)).thenReturn(Mono.just(AcademicCalendar.builder()
                .id(1)
                .status(CalendarStatus.ACTIVE)
                .build()));

        StepVerifier.create(calendarService.reactivateCalendar(1))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}
