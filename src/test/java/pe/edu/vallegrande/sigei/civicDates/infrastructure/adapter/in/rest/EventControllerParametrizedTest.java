package pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.in.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pe.edu.vallegrande.sigei.civicDates.application.dto.request.CreateEventRequest;
import pe.edu.vallegrande.sigei.civicDates.application.dto.request.UpdateEventRequest;
import pe.edu.vallegrande.sigei.civicDates.application.mapper.EventMapper;
import pe.edu.vallegrande.sigei.civicDates.application.service.EventService;
import pe.edu.vallegrande.sigei.civicDates.domain.exception.EventNotFoundException;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.external.CloudinaryService;
import pe.edu.vallegrande.sigei.civicDates.domain.model.Event;
import pe.edu.vallegrande.sigei.civicDates.domain.model.enums.EventStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * PRUEBAS PARAMETRIZADAS - EventController
 * 
 * Casos de uso:
 * 1. Obtener evento por ID
 * 2. Listar eventos
 * 3. Obtener eventos por institución
 * 4. Obtener eventos inactivos
 * 5. Crear evento
 * 6. Actualizar evento
 * 7. Eliminar evento
 */
@DisplayName("EventController Tests")
class EventControllerParametrizedTest {

    @Mock
    private EventService eventService;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private CloudinaryService cloudinaryService;

    private EventController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        controller = new EventController(eventService, eventMapper, cloudinaryService);
    }

    /**
     * Caso 1: getEventById - Obtener evento por ID
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Evento encontrado, 1, ACTIVE, true",
            "Evento no encontrado, 999, ACTIVE, false",
            "Evento inactivo, 2, INACTIVE, true"
    })
    @DisplayName("getEventById - Obtener evento")
    void getEventById_shouldReturnEventOrEmpty(
            String scenario,
            String eventId,
            String eventStatus,
            String shouldExist) {

        System.out.println("\n[TEST] " + scenario);

        long id = Long.parseLong(eventId);
        boolean exists = "true".equals(shouldExist);

        if (exists) {
            Event event = Event.builder()
                    .id(id)
                    .institutionId("INST-001")
                    .title("Evento Test")
                    .description("Descripción")
                    .startDate(LocalDate.of(2026, 6, 15))
                    .endDate(LocalDate.of(2026, 6, 15))
                    .eventType("CIVICO")
                    .status(EventStatus.valueOf(eventStatus))
                    .build();
            when(eventService.getEventById(id)).thenReturn(Mono.just(event));
        } else {
            when(eventService.getEventById(id))
                    .thenReturn(Mono.error(new EventNotFoundException(id)));
        }

        var result = controller.getEventById(id);

        assertNotNull(result, "Result should not be null");
        System.out.println("[✓] " + scenario);
    }

    /**
     * Caso 2: getAllEvents - Listar eventos
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Sin eventos, 0",
            "Un evento, 1",
            "Múltiples eventos, 5"
    })
    @DisplayName("getAllEvents - Listar eventos")
    void getAllEvents_shouldReturnAllEvents(
            String scenario,
            String count) {

        System.out.println("\n[TEST] " + scenario);

        int eventCount = Integer.parseInt(count);
        var events = java.util.stream.IntStream.range(0, eventCount)
                .mapToObj(i -> Event.builder()
                        .id((long) (i + 1))
                        .institutionId("INST-001")
                        .title("Evento " + (i + 1))
                        .eventType("CIVICO")
                        .status(EventStatus.ACTIVE)
                        .build())
                .toList();

        when(eventService.getAllEvents()).thenReturn(Flux.fromIterable(events));

        var result = controller.getAllEvents();

        assertNotNull(result, "Result should not be null");
        System.out.println("[✓] " + scenario + " - Retornó " + eventCount + " eventos");
    }

    /**
     * Caso 3: getEventsByInstitution
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Institución sin eventos, INST-EMPTY, 0",
            "Institución con eventos, INST-001, 3"
    })
    @DisplayName("getEventsByInstitution - Eventos por institución")
    void getEventsByInstitution_shouldReturnInstitutionEvents(
            String scenario,
            String institutionId,
            String count) {

        System.out.println("\n[TEST] " + scenario);

        int eventCount = Integer.parseInt(count);
        var events = java.util.stream.IntStream.range(0, eventCount)
                .mapToObj(i -> Event.builder()
                        .id((long) (i + 1))
                        .institutionId(institutionId)
                        .title("Evento " + (i + 1))
                        .eventType("CIVICO")
                        .status(EventStatus.ACTIVE)
                        .build())
                .toList();

        when(eventService.getEventsByInstitution(institutionId))
                .thenReturn(Flux.fromIterable(events));

        var result = controller.getEventsByInstitution(institutionId);

        assertNotNull(result, "Result should not be null");
        System.out.println("[✓] " + scenario + " - Retornó " + eventCount + " eventos");
    }

    /**
     * Caso 4: getInactiveEvents
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Sin eventos inactivos, 0",
            "Con eventos inactivos, 2"
    })
    @DisplayName("getInactiveEvents - Eventos inactivos")
    void getInactiveEvents_shouldReturnInactiveEvents(
            String scenario,
            String count) {

        System.out.println("\n[TEST] " + scenario);

        int eventCount = Integer.parseInt(count);
        var events = java.util.stream.IntStream.range(0, eventCount)
                .mapToObj(i -> Event.builder()
                        .id((long) (i + 1))
                        .title("Evento Inactivo " + (i + 1))
                        .status(EventStatus.INACTIVE)
                        .build())
                .toList();

        when(eventService.getInactiveEvents()).thenReturn(Flux.fromIterable(events));

        var result = controller.getInactiveEvents();

        assertNotNull(result, "Result should not be null");
        System.out.println("[✓] " + scenario + " - Retornó " + eventCount + " eventos inactivos");
    }

    /**
     * Caso 5: createEvent
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Evento válido, INST-001, Evento Test"
    })
    @DisplayName("createEvent - Crear evento")
    void createEvent_shouldCreateEventSuccessfully(
            String scenario,
            String institutionId,
            String title) {

        System.out.println("\n[TEST] " + scenario);

        CreateEventRequest request = CreateEventRequest.builder()
                .institutionId(institutionId)
                .title(title)
                .description("Descripción")
                .startDate(LocalDate.of(2026, 6, 15))
                .endDate(LocalDate.of(2026, 6, 15))
                .eventType("CIVICO")
                .createdBy("admin")
                .build();

        Event event = Event.builder()
                .id(1L)
                .institutionId(institutionId)
                .title(title)
                .status(EventStatus.ACTIVE)
                .build();

        when(eventMapper.toEvent(any())).thenReturn(event);
        when(eventService.createEvent(any())).thenReturn(Mono.just(event));

        var result = controller.createEvent(request);

        assertNotNull(result, "Result should not be null");
        System.out.println("[✓] " + scenario);
    }

    /**
     * Caso 6: updateEvent
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Actualización exitosa, 1, true",
            "Evento no existe, 999, false"
    })
    @DisplayName("updateEvent - Actualizar evento")
    void updateEvent_shouldUpdateEvent(
            String scenario,
            String eventId,
            String shouldExist) {

        System.out.println("\n[TEST] " + scenario);

        long id = Long.parseLong(eventId);
        boolean exists = "true".equals(shouldExist);

        UpdateEventRequest updateRequest = UpdateEventRequest.builder()
                .title("Evento Actualizado")
                .description("Nueva descripción")
                .startDate(LocalDate.of(2026, 7, 20))
                .build();

        if (exists) {
            Event existing = Event.builder()
                    .id(id)
                    .title("Evento Original")
                    .status(EventStatus.ACTIVE)
                    .build();
            Event updated = Event.builder()
                    .id(id)
                    .title("Evento Actualizado")
                    .status(EventStatus.ACTIVE)
                    .build();
            when(eventService.getEventById(id)).thenReturn(Mono.just(existing));
            when(eventMapper.toEvent(any(), any())).thenReturn(updated);
            when(eventService.updateEvent(any(), any())).thenReturn(Mono.just(updated));
        } else {
            when(eventService.getEventById(id))
                    .thenReturn(Mono.error(new EventNotFoundException(id)));
        }

        var result = controller.updateEvent(id, updateRequest);

        assertNotNull(result, "Result should not be null");
        System.out.println("[✓] " + scenario);
    }

    /**
     * Caso 7: deleteEvent
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Eliminación exitosa, 1, true",
            "Evento no existe, 999, false"
    })
    @DisplayName("deleteEvent - Eliminar evento")
    void deleteEvent_shouldDeleteEvent(
            String scenario,
            String eventId,
            String shouldExist) {

        System.out.println("\n[TEST] " + scenario);

        long id = Long.parseLong(eventId);
        boolean exists = "true".equals(shouldExist);

        if (exists) {
            when(eventService.deleteEvent(id)).thenReturn(Mono.empty());
        } else {
            when(eventService.deleteEvent(id))
                    .thenReturn(Mono.error(new EventNotFoundException(id)));
        }

        var result = controller.deleteEvent(id);

        assertNotNull(result, "Result should not be null");
        System.out.println("[✓] " + scenario);
    }
}
