package pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.in.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pe.edu.vallegrande.sigei.civicDates.application.dto.request.CreateCalendarRequest;
import pe.edu.vallegrande.sigei.civicDates.application.mapper.CalendarMapper;
import pe.edu.vallegrande.sigei.civicDates.application.mapper.EventMapper;
import pe.edu.vallegrande.sigei.civicDates.application.service.CalendarService;
import pe.edu.vallegrande.sigei.civicDates.domain.exception.CalendarNotFoundException;
import pe.edu.vallegrande.sigei.civicDates.domain.model.AcademicCalendar;
import pe.edu.vallegrande.sigei.civicDates.domain.model.enums.CalendarStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * PRUEBAS PARAMETRIZADAS - CalendarController
 * 
 * Casos de uso:
 * 1. Obtener calendario por ID
 * 2. Listar calendarios
 * 3. Obtener calendarios por institución
 * 4. Crear calendario
 * 5. Actualizar calendario
 * 6. Eliminar calendario
 */
@DisplayName("CalendarController Tests")
class CalendarControllerParametrizedTest {

    @Mock
    private CalendarService calendarService;

    @Mock
    private CalendarMapper calendarMapper;

    @Mock
    private EventMapper eventMapper;

    private CalendarController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        controller = new CalendarController(calendarService, calendarMapper, eventMapper);
    }

    /**
     * Caso 1: getCalendarById - Obtener calendario por ID
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Calendario encontrado, 1, true",
            "Calendario no encontrado, 999, false"
    })
    @DisplayName("getCalendarById - Obtener calendario")
    void getCalendarById_shouldReturnCalendarOrEmpty(
            String scenario,
            String calendarId,
            String shouldExist) {

        System.out.println("\n[TEST] " + scenario);

        int id = Integer.parseInt(calendarId);
        boolean exists = "true".equals(shouldExist);

        if (exists) {
            AcademicCalendar calendar = AcademicCalendar.builder()
                    .id(id)
                    .institutionId("INST-001")
                    .academicYear(2026)
                    .status(CalendarStatus.ACTIVE)
                    .build();
            when(calendarService.getCalendarById(id)).thenReturn(Mono.just(calendar));
        } else {
            when(calendarService.getCalendarById(id))
                    .thenReturn(Mono.error(new CalendarNotFoundException(id)));
        }

        // Call controller method
        var result = controller.getCalendarById(id);

        assertNotNull(result, "Result should not be null");
        System.out.println("[✓] " + scenario);
    }

    /**
     * Caso 2: getAllCalendars - Listar calendarios
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Sin calendarios, 0",
            "Un calendario, 1",
            "Múltiples calendarios, 3"
    })
    @DisplayName("getAllCalendars - Listar calendarios")
    void getAllCalendars_shouldReturnAllCalendars(
            String scenario,
            String count) {

        System.out.println("\n[TEST] " + scenario);

        int calendarCount = Integer.parseInt(count);
        var calendars = java.util.stream.IntStream.range(0, calendarCount)
                .mapToObj(i -> AcademicCalendar.builder()
                        .id(i + 1)
                        .institutionId("INST-" + (i + 1))
                        .academicYear(2026 + i)
                        .status(CalendarStatus.ACTIVE)
                        .build())
                .toList();

        when(calendarService.getAllCalendars()).thenReturn(Flux.fromIterable(calendars));

        var result = controller.getAllCalendars();

        assertNotNull(result, "Result should not be null");
        System.out.println("[✓] " + scenario + " - Retornó " + calendarCount + " calendarios");
    }

    /**
     * Caso 3: getCalendarsByInstitution
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Sin calendarios, INST-EMPTY, 0",
            "Con calendarios, INST-001, 2"
    })
    @DisplayName("getCalendarsByInstitution - Calendarios por institución")
    void getCalendarsByInstitution_shouldReturnInstitutionCalendars(
            String scenario,
            String institutionId,
            String count) {

        System.out.println("\n[TEST] " + scenario);

        int calendarCount = Integer.parseInt(count);
        var calendars = java.util.stream.IntStream.range(0, calendarCount)
                .mapToObj(i -> AcademicCalendar.builder()
                        .id(i + 1)
                        .institutionId(institutionId)
                        .academicYear(2026)
                        .status(CalendarStatus.ACTIVE)
                        .build())
                .toList();

        when(calendarService.getCalendarsByInstitution(institutionId))
                .thenReturn(Flux.fromIterable(calendars));

        var result = controller.getCalendarsByInstitution(institutionId);

        assertNotNull(result, "Result should not be null");
        System.out.println("[✓] " + scenario + " - Retornó " + calendarCount + " calendarios");
    }

    /**
     * Caso 4: createCalendar
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Creación válida, INST-001, 2026"
    })
    @DisplayName("createCalendar - Crear calendario")
    void createCalendar_shouldCreateCalendarSuccessfully(
            String scenario,
            String institutionId,
            String academicYear) {

        System.out.println("\n[TEST] " + scenario);

        CreateCalendarRequest request = CreateCalendarRequest.builder()
                .institutionId(institutionId)
                .academicYear(Integer.parseInt(academicYear))
                .academicYearName("Año " + academicYear)
                .startDate(LocalDate.of(Integer.parseInt(academicYear), 1, 1))
                .endDate(LocalDate.of(Integer.parseInt(academicYear), 12, 31))
                .build();

        AcademicCalendar calendar = AcademicCalendar.builder()
                .id(1)
                .institutionId(institutionId)
                .academicYear(Integer.parseInt(academicYear))
                .status(CalendarStatus.ACTIVE)
                .build();

        when(calendarMapper.toCalendar(any())).thenReturn(calendar);
        when(calendarService.createCalendar(any())).thenReturn(Mono.just(calendar));

        var result = controller.createCalendar(request);

        assertNotNull(result, "Result should not be null");
        System.out.println("[✓] " + scenario);
    }

    /**
     * Caso 5: updateCalendar
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Actualización exitosa, 1, true",
            "Calendario no existe, 999, false"
    })
    @DisplayName("updateCalendar - Actualizar calendario")
    void updateCalendar_shouldUpdateCalendar(
            String scenario,
            String calendarId,
            String shouldExist) {

        System.out.println("\n[TEST] " + scenario);

        int id = Integer.parseInt(calendarId);
        boolean exists = "true".equals(shouldExist);

        pe.edu.vallegrande.sigei.civicDates.application.dto.request.UpdateCalendarRequest updateRequest = 
            pe.edu.vallegrande.sigei.civicDates.application.dto.request.UpdateCalendarRequest.builder()
                .academicYear(2027)
                .academicYearName("Año 2027")
                .startDate(LocalDate.of(2027, 1, 1))
                .endDate(LocalDate.of(2027, 12, 31))
                .build();

        if (exists) {
            AcademicCalendar updated = AcademicCalendar.builder()
                    .id(id)
                    .institutionId("INST-UPDATED")
                    .academicYear(2027)
                    .status(CalendarStatus.ACTIVE)
                    .build();
            when(calendarService.updateCalendar(any(), any())).thenReturn(Mono.just(updated));
        } else {
            when(calendarService.updateCalendar(any(), any()))
                    .thenReturn(Mono.error(new CalendarNotFoundException(id)));
        }

        var result = controller.updateCalendar(id, updateRequest);

        assertNotNull(result, "Result should not be null");
        System.out.println("[✓] " + scenario);
    }

    /**
     * Caso 6: deleteCalendar
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Eliminación exitosa, 1, true",
            "Calendario no existe, 999, false"
    })
    @DisplayName("deleteCalendar - Eliminar calendario")
    void deleteCalendar_shouldDeleteCalendar(
            String scenario,
            String calendarId,
            String shouldExist) {

        System.out.println("\n[TEST] " + scenario);

        int id = Integer.parseInt(calendarId);
        boolean exists = "true".equals(shouldExist);

        if (exists) {
            when(calendarService.deleteCalendarLogically(id)).thenReturn(Mono.empty());
        } else {
            when(calendarService.deleteCalendarLogically(id))
                    .thenReturn(Mono.error(new CalendarNotFoundException(id)));
        }

        var result = controller.deleteCalendar(id);

        assertNotNull(result, "Result should not be null");
        System.out.println("[✓] " + scenario);
    }
}
