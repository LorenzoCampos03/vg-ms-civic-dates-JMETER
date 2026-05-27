package pe.edu.vallegrande.sigei.civicDates.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.sigei.civicDates.domain.exception.EventNotFoundException;
import pe.edu.vallegrande.sigei.civicDates.domain.model.Event;
import pe.edu.vallegrande.sigei.civicDates.domain.model.enums.EventStatus;
import pe.edu.vallegrande.sigei.civicDates.domain.port.out.EventRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

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
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @Test
        @DisplayName("Creacion de evento: asigna estado ACTIVE y timestamps")
    void createEvent_shouldAssignActiveStatusAndTimestamps() {
                System.out.println("[RUN ] createEvent_shouldAssignActiveStatusAndTimestamps");

        Event input = Event.builder()
                .institutionId("INST-001")
                .title("Acto civico")
                .startDate(LocalDate.of(2026, 6, 15))
                .endDate(LocalDate.of(2026, 6, 15))
                .build();

        when(eventRepository.save(any(Event.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(eventService.createEvent(input))
                .assertNext(saved -> {
                                        assertEquals(EventStatus.ACTIVE, saved.getStatus(), "El estado del evento debe ser ACTIVE al crearse");
                                        assertNotNull(saved.getCreatedAt(), "createdAt no debe ser null al crear evento");
                                        assertNotNull(saved.getUpdatedAt(), "updatedAt no debe ser null al crear evento");
                })
                .verifyComplete();

        verify(eventRepository).save(any(Event.class));
    }

    @Test
        @DisplayName("Actualizacion de evento inexistente: retorna EventNotFoundException")
    void updateEvent_shouldReturnEventNotFoundException_whenEventDoesNotExist() {
                System.out.println("[RUN ] updateEvent_shouldReturnEventNotFoundException_whenEventDoesNotExist");

        Long eventId = 999L;
        Event updateInput = Event.builder().title("Nuevo titulo").build();

        when(eventRepository.findById(eventId)).thenReturn(Mono.empty());

        StepVerifier.create(eventService.updateEvent(eventId, updateInput))
                .expectError(EventNotFoundException.class)
                .verify();

        verify(eventRepository).findById(eventId);
        verify(eventRepository, never()).update(any(Event.class));
    }

    @Test
        @DisplayName("Desactivacion de evento: aplica borrado logico con estado INACTIVE")
    void deleteEvent_shouldApplyLogicalDeleteSettingInactiveStatus() {
                System.out.println("[RUN ] deleteEvent_shouldApplyLogicalDeleteSettingInactiveStatus");

        Long eventId = 10L;
        Event existing = Event.builder()
                .id(eventId)
                .status(EventStatus.ACTIVE)
                .institutionId("INST-001")
                .build();

        when(eventRepository.findById(eventId)).thenReturn(Mono.just(existing));
        when(eventRepository.update(any(Event.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(eventService.deleteEvent(eventId))
                .verifyComplete();

        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).update(captor.capture());

        Event updated = captor.getValue();
                assertEquals(eventId, updated.getId(), "El id del evento actualizado debe coincidir con el solicitado");
                assertEquals(EventStatus.INACTIVE, updated.getStatus(), "El evento debe quedar INACTIVE por borrado logico");
                assertNotNull(updated.getUpdatedAt(), "updatedAt debe actualizarse al desactivar evento");
        verify(eventRepository).findById(eq(eventId));
    }
}
