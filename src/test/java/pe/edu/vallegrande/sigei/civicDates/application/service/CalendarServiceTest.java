package pe.edu.vallegrande.sigei.civicDates.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import pe.edu.vallegrande.sigei.civicDates.domain.model.AcademicCalendar;
import pe.edu.vallegrande.sigei.civicDates.domain.model.enums.CalendarStatus;
import pe.edu.vallegrande.sigei.civicDates.domain.port.out.AcademicCalendarRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

        @RegisterExtension
        TestWatcher testWatcher = new TestWatcher() {
                @Override
                public void testSuccessful(ExtensionContext context) {
                        System.out.println("[PASS] " + context.getDisplayName());
                }

                @Override
                public void testFailed(ExtensionContext context, Throwable cause) {
                        System.out.println("[FAIL] " + context.getDisplayName() + " -> " + cause.getClass().getSimpleName() + ": " + cause.getMessage());
                }
        };

    @Mock
    private AcademicCalendarRepository calendarRepository;

    @Mock
    private TransactionalOperator transactionalOperator;

    @InjectMocks
    private CalendarService calendarService;

    @Test
    @DisplayName("Editar calendario: actualiza campos permitidos")
    void updateCalendar_shouldUpdateAllowedFields() {
        System.out.println("[RUN ] updateCalendar_shouldUpdateAllowedFields");

        Integer calendarId = 1;
        Integer newAcademicYear = 2025;
        LocalDate newStartDate = LocalDate.of(2025, 1, 1);
        LocalDate newEndDate = LocalDate.of(2025, 12, 31);

        AcademicCalendar existingCalendar = AcademicCalendar.builder()
                .id(calendarId)
                .institutionId("INST-A")
                .academicYear(2024)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .status(CalendarStatus.ACTIVE)
                .build();

        AcademicCalendar updatedCalendar = AcademicCalendar.builder()
                .id(calendarId)
                .institutionId("INST-A")
                .academicYear(newAcademicYear)
                .startDate(newStartDate)
                .endDate(newEndDate)
                .status(CalendarStatus.ACTIVE)
                .build();

        when(calendarRepository.findById(calendarId)).thenReturn(Mono.just(existingCalendar));
        when(calendarRepository.save(any(AcademicCalendar.class))).thenReturn(Mono.just(updatedCalendar));

        AcademicCalendar updateRequest = AcademicCalendar.builder()
                .academicYear(newAcademicYear)
                .startDate(newStartDate)
                .endDate(newEndDate)
                .build();

        StepVerifier.create(calendarService.updateCalendar(calendarId, updateRequest))
                .assertNext(result -> {
                    assertEquals(newAcademicYear, result.getAcademicYear());
                    assertEquals(newStartDate, result.getStartDate());
                    assertEquals(newEndDate, result.getEndDate());
                })
                .verifyComplete();

        verify(calendarRepository).save(any(AcademicCalendar.class));
    }

    @Test
    @DisplayName("Obtener calendarios: filtra eliminados logicamente")
    void getAllCalendars_shouldFilterLogicallyDeletedCalendars() {
        System.out.println("[RUN ] getAllCalendars_shouldFilterLogicallyDeletedCalendars");

        AcademicCalendar activeCalendar = AcademicCalendar.builder()
                .id(1)
                .status(CalendarStatus.ACTIVE)
                .build();

        AcademicCalendar inactiveCalendar = AcademicCalendar.builder()
                .id(2)
                .status(CalendarStatus.INACTIVE)
                .build();

        when(calendarRepository.findAll()).thenReturn(Flux.just(activeCalendar, inactiveCalendar));

        StepVerifier.create(calendarService.getAllCalendars())
                .assertNext(result -> {
                    assertEquals(1, result.getId());
                    assertEquals(CalendarStatus.ACTIVE, result.getStatus());
                })
                .verifyComplete();
    }
}
