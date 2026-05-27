package pe.edu.vallegrande.sigei.civicDates.application.mapper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.BeforeEach;
import pe.edu.vallegrande.sigei.civicDates.application.dto.request.CreateEventRequest;
import pe.edu.vallegrande.sigei.civicDates.application.dto.request.UpdateEventRequest;
import pe.edu.vallegrande.sigei.civicDates.application.dto.response.EventResponse;
import pe.edu.vallegrande.sigei.civicDates.domain.model.Event;
import pe.edu.vallegrande.sigei.civicDates.domain.model.enums.EventStatus;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PRUEBAS PARAMETRIZADAS - EventMapper
 * 
 * Casos de uso:
 * 1. Convertir CreateEventRequest → Event
 * 2. Convertir UpdateEventRequest + Event → Event (actualización)
 * 3. Convertir Event → EventResponse
 */
class EventMapperParametrizedTest {

    private EventMapper eventMapper;

    @BeforeEach
    void setup() {
        eventMapper = new EventMapper();
    }

    /**
     * Caso 1: Mapear CreateEventRequest a Event
     * 
     * Escenarios:
     * - Evento Cívico sin recurrencia
     * - Evento Cultural con recurrencia
     * - Evento Religioso nacional
     * - Evento Institucional que afecta clases
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Evento Cívico, INST-A, Acto Cívico, CIVICO, 2026-06-15, 2026-06-15, false, false, false, false, User1",
            "Evento Cultural, INST-B, Festival, CULTURAL, 2026-07-20, 2026-07-22, true, false, false, false, User2",
            "Evento Religioso, INST-C, Navidad, RELIGIOSO, 2026-12-25, 2026-12-25, false, false, true, false, User3",
            "Evento Institucional, INST-D, Reunión, INSTITUCIONAL, 2026-05-10, 2026-05-10, false, true, false, true, User4"
    })
    void toEvent_shouldMapCreateEventRequestToEntity(
            String scenario,
            String institutionId,
            String title,
            String eventType,
            String startDate,
            String endDate,
            String isRecurring,
            String isHoliday,
            String isNational,
            String affectsClasses,
            String createdBy) {

        System.out.println("\n[TEST] " + scenario);

        CreateEventRequest request = CreateEventRequest.builder()
                .institutionId(institutionId)
                .title(title)
                .description("Descripción de " + title)
                .startDate(LocalDate.parse(startDate))
                .endDate(LocalDate.parse(endDate))
                .eventType(eventType)
                .isRecurring("true".equals(isRecurring))
                .isHoliday("true".equals(isHoliday))
                .isNational("true".equals(isNational))
                .affectsClasses("true".equals(affectsClasses))
                .createdBy(createdBy)
                .build();

        Event result = eventMapper.toEvent(request);

        assertNotNull(result, "El resultado no debe ser null");
        assertEquals(institutionId, result.getInstitutionId(), "Institution ID debe coincidir");
        assertEquals(title, result.getTitle(), "Título debe coincidir");
        assertEquals(eventType, result.getEventType(), "Tipo de evento debe coincidir");
        assertEquals(EventStatus.ACTIVE, result.getStatus(), "Estado debe ser ACTIVE");
        assertEquals("true".equals(isRecurring), result.getIsRecurring(), "IsRecurring debe coincidir");
        assertEquals("true".equals(isNational), result.getIsNational(), "IsNational debe coincidir");
        assertNotNull(result.getCreatedAt(), "CreatedAt debe establecerse");
        assertNotNull(result.getUpdatedAt(), "UpdatedAt debe establecerse");

        System.out.println("[✓] " + scenario);
    }

    /**
     * Caso 2: Actualizar Event con UpdateEventRequest
     * 
     * Escenarios:
     * - Cambiar título
     * - Cambiar fechas
     * - Cambiar propiedades booleanas
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Cambiar título, Nuevo Título, 2026-06-15",
            "Cambiar fechas, Mismo Título, 2026-07-20",
            "Cambiar propiedades, Otro Título, 2026-05-10"
    })
    void toEvent_shouldUpdateEventFromUpdateRequest(
            String scenario,
            String newTitle,
            String newStartDate) {

        System.out.println("\n[TEST] " + scenario);

        Event existingEvent = Event.builder()
                .id(100L)
                .institutionId("INST-001")
                .title("Título Original")
                .description("Descripción Original")
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 1, 1))
                .eventType("CIVICO")
                .isRecurring(false)
                .status(EventStatus.ACTIVE)
                .build();

        UpdateEventRequest updateRequest = UpdateEventRequest.builder()
                .title(newTitle)
                .description("Descripción Actualizada")
                .startDate(LocalDate.parse(newStartDate))
                .endDate(LocalDate.parse(newStartDate))
                .eventType("CULTURAL")
                .isRecurring(true)
                .isHoliday(false)
                .isNational(true)
                .affectsClasses(true)
                .build();

        Event result = eventMapper.toEvent(updateRequest, existingEvent);

        assertNotNull(result, "El resultado no debe ser null");
        assertEquals(100L, result.getId(), "ID debe mantenerse");
        assertEquals(newTitle, result.getTitle(), "Título debe actualizarse");
        assertEquals("Descripción Actualizada", result.getDescription(), "Descripción debe actualizarse");
        assertEquals(LocalDate.parse(newStartDate), result.getStartDate(), "Fecha inicio debe actualizarse");
        assertEquals("CULTURAL", result.getEventType(), "Tipo de evento debe actualizarse");
        assertTrue(result.getIsRecurring(), "IsRecurring debe actualizarse");
        assertNotNull(result.getUpdatedAt(), "UpdatedAt debe actualizarse");

        System.out.println("[✓] " + scenario);
    }

    /**
     * Caso 3: Mapear Event a EventResponse
     * 
     * Escenarios:
     * - Evento ACTIVE con todos los datos
     * - Evento INACTIVE
     * - Evento con campos opcionales
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Evento Activo, 1, ACTIVE, Acto Cívico",
            "Evento Inactivo, 2, INACTIVE, Evento Cancelado",
            "Evento Futuro, 3, ACTIVE, Próximo Evento"
    })
    void toResponse_shouldMapEventToResponse(
            String scenario,
            String eventId,
            String status,
            String title) {

        System.out.println("\n[TEST] " + scenario);

        Event event = Event.builder()
                .id(Long.parseLong(eventId))
                .institutionId("INST-001")
                .title(title)
                .description("Descripción del evento")
                .startDate(LocalDate.of(2026, 6, 15))
                .endDate(LocalDate.of(2026, 6, 15))
                .eventType("CIVICO")
                .isHoliday(true)
                .isRecurring(false)
                .isNational(false)
                .affectsClasses(true)
                .createdBy("admin")
                .status(EventStatus.valueOf(status))
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        EventResponse result = eventMapper.toResponse(event);

        assertNotNull(result, "El resultado no debe ser null");
        assertEquals(Long.parseLong(eventId), result.getId(), "ID debe coincidir");
        assertEquals("INST-001", result.getInstitutionId(), "Institution ID debe coincidir");
        assertEquals(title, result.getTitle(), "Título debe coincidir");
        assertEquals(EventStatus.valueOf(status), result.getStatus(), "Estado debe coincidir");
        assertEquals("CIVICO", result.getEventType(), "Tipo de evento debe coincidir");
        assertEquals(true, result.getIsHoliday(), "IsHoliday debe coincidir");
        assertNotNull(result.getCreatedAt(), "CreatedAt debe estar presente");
        assertNotNull(result.getUpdatedAt(), "UpdatedAt debe estar presente");

        System.out.println("[✓] " + scenario);
    }
}
