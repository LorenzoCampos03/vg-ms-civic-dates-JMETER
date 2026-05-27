package pe.edu.vallegrande.sigei.civicDates.application.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import pe.edu.vallegrande.sigei.civicDates.domain.model.AcademicCalendar;
import pe.edu.vallegrande.sigei.civicDates.domain.model.enums.CalendarStatus;
import pe.edu.vallegrande.sigei.civicDates.domain.port.out.AcademicCalendarRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarServiceParametrizedTest {

    @Mock
    private AcademicCalendarRepository calendarRepository;

    @Mock
    private TransactionalOperator transactionalOperator;

    @InjectMocks
    private CalendarService calendarService;

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Solo año académico, 2025, , , , 2025, 2024, 2024-01-01, 2024-12-31",
            "Solo nombre del año, , 2024-2025, , , 2024, 2024-2025, 2024-01-01, 2024-12-31",
            "Solo fecha inicio, , , 2024-02-01, , 2024, 2024, 2024-02-01, 2024-12-31",
            "Solo fecha fin, , , , 2024-11-30, 2024, 2024, 2024-01-01, 2024-11-30",
            "Año + fechas, 2025, , 2025-01-01, 2025-12-31, 2025, 2024, 2025-01-01, 2025-12-31",
            "Sin cambios, , , , , 2024, 2024, 2024-01-01, 2024-12-31",
            "Todos los campos, 2026, Año 2026, 2026-03-01, 2026-10-31, 2026, Año 2026, 2026-03-01, 2026-10-31"
    })
    void updateCalendar_shouldSelectivelyUpdateFields(
            String scenario,
            String newYear,
            String newYearName,
            String newStartDate,
            String newEndDate,
            String expectedYear,
            String expectedYearName,
            String expectedStartDate,
            String expectedEndDate) {

        System.out.println("\n[TEST] " + scenario);

        AcademicCalendar existing = AcademicCalendar.builder()
                .id(1)
                .institutionId("INST-001")
                .academicYear(2024)
                .academicYearName("2024")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .status(CalendarStatus.ACTIVE)
                .build();

        AcademicCalendar updateRequest = AcademicCalendar.builder()
                .academicYear(newYear == null || newYear.isEmpty() ? null : Integer.parseInt(newYear))
                .academicYearName(newYearName == null || newYearName.isEmpty() ? null : newYearName)
                .startDate(newStartDate == null || newStartDate.isEmpty() ? null : LocalDate.parse(newStartDate))
                .endDate(newEndDate == null || newEndDate.isEmpty() ? null : LocalDate.parse(newEndDate))
                .build();

        AcademicCalendar updated = AcademicCalendar.builder()
                .id(1)
                .institutionId("INST-001")
                .academicYear(Integer.parseInt(expectedYear))
                .academicYearName(expectedYearName)
                .startDate(LocalDate.parse(expectedStartDate))
                .endDate(LocalDate.parse(expectedEndDate))
                .status(CalendarStatus.ACTIVE)
                .build();

        when(calendarRepository.findById(1)).thenReturn(Mono.just(existing));
        when(calendarRepository.save(any(AcademicCalendar.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(calendarService.updateCalendar(1, updateRequest))
                .assertNext(result -> {
                    assertEquals(Integer.parseInt(expectedYear), result.getAcademicYear());
                    assertEquals(expectedYearName, result.getAcademicYearName());
                    assertEquals(LocalDate.parse(expectedStartDate), result.getStartDate());
                    assertEquals(LocalDate.parse(expectedEndDate), result.getEndDate());
                    System.out.println("[✓] " + scenario);
                })
                .verifyComplete();
    }
}
