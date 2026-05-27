package pe.edu.vallegrande.sigei.civicDates.application.mapper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.BeforeEach;
import pe.edu.vallegrande.sigei.civicDates.application.dto.request.CreateCalendarRequest;
import pe.edu.vallegrande.sigei.civicDates.application.dto.response.CalendarResponse;
import pe.edu.vallegrande.sigei.civicDates.application.dto.response.CalendarWithEventsResponse;
import pe.edu.vallegrande.sigei.civicDates.application.dto.response.EventResponse;
import pe.edu.vallegrande.sigei.civicDates.domain.model.AcademicCalendar;
import pe.edu.vallegrande.sigei.civicDates.domain.model.enums.CalendarStatus;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PRUEBAS PARAMETRIZADAS - CalendarMapper
 * 
 * Casos de uso:
 * 1. Convertir CreateCalendarRequest → AcademicCalendar
 * 2. Convertir AcademicCalendar → CalendarResponse
 * 3. Convertir AcademicCalendar + eventos → CalendarWithEventsResponse
 */
class CalendarMapperParametrizedTest {

    private CalendarMapper calendarMapper;

    @BeforeEach
    void setup() {
        calendarMapper = new CalendarMapper();
    }

    /**
     * Caso 1: Mapear CreateCalendarRequest a AcademicCalendar
     * 
     * Escenarios:
     * - Solicitud con todos los campos
     * - Solicitud con institución diferente
     * - Solicitud con años consecutivos
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Calendario 2026, INST-A, 2026, Año 2026, 2026-01-01, 2026-12-31",
            "Calendario 2025, INST-B, 2025, Año 2025, 2025-02-01, 2025-11-30",
            "Calendario 2027, INST-C, 2027, Año 2027, 2027-03-15, 2027-10-15"
    })
    void toCalendar_shouldMapCreateCalendarRequestToEntity(
            String scenario,
            String institutionId,
            String academicYear,
            String academicYearName,
            String startDate,
            String endDate) {

        System.out.println("\n[TEST] " + scenario);

        CreateCalendarRequest request = CreateCalendarRequest.builder()
                .institutionId(institutionId)
                .academicYear(Integer.parseInt(academicYear))
                .academicYearName(academicYearName)
                .startDate(LocalDate.parse(startDate))
                .endDate(LocalDate.parse(endDate))
                .build();

        AcademicCalendar result = calendarMapper.toCalendar(request);

        assertNotNull(result, "El resultado no debe ser null");
        assertEquals(institutionId, result.getInstitutionId(), "Institution ID debe coincidir");
        assertEquals(Integer.parseInt(academicYear), result.getAcademicYear(), "Año debe coincidir");
        assertEquals(academicYearName, result.getAcademicYearName(), "Nombre del año debe coincidir");
        assertEquals(LocalDate.parse(startDate), result.getStartDate(), "Fecha inicio debe coincidir");
        assertEquals(LocalDate.parse(endDate), result.getEndDate(), "Fecha fin debe coincidir");
        assertNotNull(result.getCreatedAt(), "CreatedAt debe establecerse");
        assertNotNull(result.getUpdatedAt(), "UpdatedAt debe establecerse");

        System.out.println("[✓] " + scenario);
    }

    /**
     * Caso 2: Mapear AcademicCalendar a CalendarResponse
     * 
     * Escenarios:
     * - Calendario ACTIVE
     * - Calendario INACTIVE
     * - Calendario con datos completos
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Calendario Activo, INST-A, 2026, ACTIVE, 1",
            "Calendario Inactivo, INST-B, 2025, INACTIVE, 2",
            "Calendario Archivado, INST-C, 2024, INACTIVE, 3"
    })
    void toResponse_shouldMapAcademicCalendarToResponse(
            String scenario,
            String institutionId,
            String academicYear,
            String status,
            String calendarId) {

        System.out.println("\n[TEST] " + scenario);

        AcademicCalendar calendar = AcademicCalendar.builder()
                .id(Integer.parseInt(calendarId))
                .institutionId(institutionId)
                .academicYear(Integer.parseInt(academicYear))
                .academicYearName("Año " + academicYear)
                .startDate(LocalDate.of(Integer.parseInt(academicYear), 1, 1))
                .endDate(LocalDate.of(Integer.parseInt(academicYear), 12, 31))
                .status(CalendarStatus.valueOf(status))
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        CalendarResponse result = calendarMapper.toResponse(calendar);

        assertNotNull(result, "El resultado no debe ser null");
        assertEquals(Integer.parseInt(calendarId), result.getId(), "ID debe coincidir");
        assertEquals(institutionId, result.getInstitutionId(), "Institution ID debe coincidir");
        assertEquals(Integer.parseInt(academicYear), result.getAcademicYear(), "Año debe coincidir");
        assertEquals(CalendarStatus.valueOf(status), result.getStatus(), "Estado debe coincidir");
        assertNotNull(result.getCreatedAt(), "CreatedAt debe estar presente");
        assertNotNull(result.getUpdatedAt(), "UpdatedAt debe estar presente");

        System.out.println("[✓] " + scenario);
    }

    /**
     * Caso 3: Mapear AcademicCalendar + eventos a CalendarWithEventsResponse
     * 
     * Escenarios:
     * - Calendario sin eventos
     * - Calendario con 1 evento
     * - Calendario con múltiples eventos
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Calendario sin eventos, 1, 0",
            "Calendario con 1 evento, 2, 1",
            "Calendario con 3 eventos, 3, 3"
    })
    void toResponseWithEvents_shouldMapCalendarWithEventsList(
            String scenario,
            String calendarId,
            String eventCount) {

        System.out.println("\n[TEST] " + scenario);

        AcademicCalendar calendar = AcademicCalendar.builder()
                .id(Integer.parseInt(calendarId))
                .institutionId("INST-001")
                .academicYear(2026)
                .academicYearName("Año 2026")
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 12, 31))
                .status(CalendarStatus.ACTIVE)
                .build();

        List<EventResponse> events = createEventResponses(Integer.parseInt(eventCount));

        CalendarWithEventsResponse result = calendarMapper.toResponseWithEvents(calendar, events);

        assertNotNull(result, "El resultado no debe ser null");
        assertEquals(Integer.parseInt(calendarId), result.getId(), "ID debe coincidir");
        assertEquals(Integer.parseInt(eventCount), result.getEvents().size(), "Cantidad de eventos debe coincidir");
        assertEquals("Año 2026", result.getAcademicYearName(), "Nombre del año debe coincidir");

        System.out.println("[✓] " + scenario + " - Mapeó " + eventCount + " eventos");
    }

    private List<EventResponse> createEventResponses(int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> EventResponse.builder()
                        .id((long) (i + 1))
                        .title("Evento " + (i + 1))
                        .institutionId("INST-001")
                        .build())
                .toList();
    }
}
