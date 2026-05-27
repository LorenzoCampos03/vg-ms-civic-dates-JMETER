package pe.edu.vallegrande.sigei.civicDates.application.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.sigei.civicDates.domain.exception.EventNotFoundException;
import pe.edu.vallegrande.sigei.civicDates.domain.model.Event;
import pe.edu.vallegrande.sigei.civicDates.domain.model.enums.EventStatus;
import pe.edu.vallegrande.sigei.civicDates.domain.port.out.EventRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * PRUEBAS PARAMETRIZADAS - EventService
 * 
 * Casos de uso:
 * 1. Crear eventos con diferentes tipos
 * 2. Obtener eventos por institución
 * 3. Actualizar eventos con campos selectivos
 * 4. Cambiar estado de eventos (ACTIVE/INACTIVE)
 */
@ExtendWith(MockitoExtension.class)
class EventServiceParametrizedTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    /**
     * Caso 1: Crear eventos de diferentes tipos
     * 
     * Escenarios:
     * - Evento Cívico
     * - Evento Cultural
     * - Evento Religioso
     * - Evento Institucional
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Evento Cívico, CIVICO, Acto Cívico Patrio, 2026-06-15, 2026-06-15, true, false",
            "Evento Cultural, CULTURAL, Festival Cultural, 2026-07-20, 2026-07-22, false, false",
            "Evento Religioso, RELIGIOSO, Celebración Religiosa, 2026-12-25, 2026-12-25, false, false",
            "Evento Institucional, INSTITUCIONAL, Reunión Directiva, 2026-05-10, 2026-05-10, false, true"
    })
    void createEvent_shouldCreateDifferentEventTypes(
            String scenario,
            String eventType,
            String title,
            String startDate,
            String endDate,
            String affectsClasses,
            String isNational) {

        System.out.println("\n[TEST] " + scenario);

        Event input = Event.builder()
                .institutionId("INST-001")
                .title(title)
                .eventType(eventType)
                .startDate(LocalDate.parse(startDate))
                .endDate(LocalDate.parse(endDate))
                .affectsClasses("true".equals(affectsClasses))
                .isNational("true".equals(isNational))
                .description("Descripción de " + title)
                .build();

        Event saved = Event.builder()
                .id(1L)
                .institutionId("INST-001")
                .title(title)
                .eventType(eventType)
                .startDate(LocalDate.parse(startDate))
                .endDate(LocalDate.parse(endDate))
                .affectsClasses("true".equals(affectsClasses))
                .isNational("true".equals(isNational))
                .status(EventStatus.ACTIVE)
                .description("Descripción de " + title)
                .build();

        when(eventRepository.save(any(Event.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(eventService.createEvent(input))
                .assertNext(result -> {
                    assertEquals(EventStatus.ACTIVE, result.getStatus());
                    assertEquals(eventType, result.getEventType());
                    assertEquals(title, result.getTitle());
                    System.out.println("[✓] " + scenario);
                })
                .verifyComplete();
    }

    /**
     * Caso 2: Obtener eventos por institución
     * 
     * Escenarios:
     * - Institución con 1 evento
     * - Institución con múltiples eventos
     * - Institución sin eventos
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Una institución con 1 evento, INST-A, 1",
            "Una institución con 3 eventos, INST-B, 3",
            "Una institución sin eventos, INST-C, 0"
    })
    void getEventsByInstitution_shouldFilterByInstitution(
            String scenario,
            String institutionId,
            int eventCount) {

        System.out.println("\n[TEST] " + scenario);

        if (eventCount == 0) {
            when(eventRepository.findByInstitutionId(institutionId)).thenReturn(Flux.empty());
        } else {
            Event[] events = new Event[eventCount];
            for (int i = 0; i < eventCount; i++) {
                events[i] = Event.builder()
                        .id((long) (i + 1))
                        .institutionId(institutionId)
                        .title("Evento " + (i + 1))
                        .status(EventStatus.ACTIVE)
                        .build();
            }
            when(eventRepository.findByInstitutionId(institutionId)).thenReturn(Flux.fromArray(events));
        }

        StepVerifier.create(eventService.getEventsByInstitution(institutionId))
                .expectNextCount(eventCount)
                .verifyComplete();

        System.out.println("[✓] " + scenario + " - Retornó " + eventCount + " eventos");
    }

    /**
     * Caso 3: Actualizar evento selectivamente
     * 
     * Escenarios:
     * - Solo actualizar título
     * - Solo actualizar descripción
     * - Solo cambiar tipo de evento
     * - Actualizar múltiples campos
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Solo título, Nuevo Título, , , CIVICO",
            "Solo descripción, Título Original, Nueva Descripción, , CIVICO",
            "Cambiar tipo, Título Original, , , CULTURAL",
            "Múltiples campos, Nuevo Título, Nueva Descripción, , RELIGIOSO"
    })
    void updateEvent_shouldSelectivelyUpdateEventFields(
            String scenario,
            String newTitle,
            String newDescription,
            String newEventType,
            String baseEventType) {

        System.out.println("\n[TEST] " + scenario);

        Long eventId = 100L;
        
        Event existing = Event.builder()
                .id(eventId)
                .institutionId("INST-001")
                .title("Título Original")
                .description("Descripción Original")
                .eventType("CIVICO")
                .status(EventStatus.ACTIVE)
                .build();

        Event updateRequest = Event.builder()
                .title(newTitle.isEmpty() ? null : newTitle)
                .description(newDescription == null || newDescription.isEmpty() ? null : newDescription)
                .eventType(newEventType == null || newEventType.isEmpty() ? null : newEventType)
                .build();

        Event updated = Event.builder()
                .id(eventId)
                .institutionId("INST-001")
                .title(newTitle.isEmpty() ? "Título Original" : newTitle)
                .description(newDescription == null || newDescription.isEmpty() ? "Descripción Original" : newDescription)
                .eventType(newEventType == null || newEventType.isEmpty() ? "CIVICO" : newEventType)
                .status(EventStatus.ACTIVE)
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Mono.just(existing));
        when(eventRepository.update(any(Event.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(eventService.updateEvent(eventId, updateRequest))
                .assertNext(result -> {
                    assertEquals(eventId, result.getId());
                    assertEquals(EventStatus.ACTIVE, result.getStatus());
                    System.out.println("[✓] " + scenario);
                })
                .verifyComplete();
    }

    /**
     * Caso 4: Cambios de estado (ACTIVE ↔ INACTIVE)
     * 
     * Escenarios:
     * - Borrar evento activo → INACTIVE
     * - Restaurar evento inactivo → ACTIVE
     * - Restaurar evento ya activo → sin cambios
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Desactivar evento ACTIVE, 1, ACTIVE, INACTIVE, delete",
            "Reactivar evento INACTIVE, 2, INACTIVE, ACTIVE, restore",
            "Reactivar evento ya ACTIVE, 3, ACTIVE, ACTIVE, restore"
    })
    void eventStatusTransitions_shouldHandleStateChanges(
            String scenario,
            Long eventId,
            String currentStatus,
            String expectedStatus,
            String operation) {

        System.out.println("\n[TEST] " + scenario);

        Event existing = Event.builder()
                .id(eventId)
                .institutionId("INST-001")
                .title("Evento " + eventId)
                .status(EventStatus.valueOf(currentStatus))
                .build();

        Event resultEvent = Event.builder()
                .id(eventId)
                .institutionId("INST-001")
                .title("Evento " + eventId)
                .status(EventStatus.valueOf(expectedStatus))
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Mono.just(existing));
        when(eventRepository.update(any(Event.class))).thenReturn(Mono.just(resultEvent));

        if ("delete".equals(operation)) {
            StepVerifier.create(eventService.deleteEvent(eventId))
                    .expectComplete()
                    .verify();
        } else {
            StepVerifier.create(eventService.restoreEvent(eventId))
                    .assertNext(result -> {
                        assertEquals(eventId, result.getId());
                        assertEquals(EventStatus.valueOf(expectedStatus), result.getStatus());
                    })
                    .verifyComplete();
        }

        System.out.println("[✓] " + scenario + " - Cambió de " + currentStatus + " a " + expectedStatus);
    }

    /**
     * Caso 5: Manejo de errores - Evento no encontrado
     * 
     * Escenarios:
     * - Obtener evento inexistente
     * - Actualizar evento inexistente
     * - Borrar evento inexistente
     */
    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Obtener evento inexistente, 999, get",
            "Actualizar evento inexistente, 999, update",
            "Borrar evento inexistente, 999, delete"
    })
    void eventOperations_shouldThrowEventNotFoundExceptionForNonExistentEvents(
            String scenario,
            Long eventId,
            String operation) {

        System.out.println("\n[TEST] " + scenario);

        when(eventRepository.findById(eventId)).thenReturn(Mono.empty());

        Mono<?> result;
        if ("get".equals(operation)) {
            result = eventService.getEventById(eventId);
        } else if ("update".equals(operation)) {
            result = eventService.updateEvent(eventId, Event.builder().title("Nuevo").build());
        } else {
            result = eventService.deleteEvent(eventId);
        }

        StepVerifier.create(result)
                .expectError(EventNotFoundException.class)
                .verify();

        System.out.println("[✓] " + scenario + " - Lanzó EventNotFoundException");
    }
}
