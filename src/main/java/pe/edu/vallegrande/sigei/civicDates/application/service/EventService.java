package pe.edu.vallegrande.sigei.civicDates.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.sigei.civicDates.domain.exception.EventNotFoundException;
import pe.edu.vallegrande.sigei.civicDates.domain.model.Event;
import pe.edu.vallegrande.sigei.civicDates.domain.model.enums.EventStatus;
import pe.edu.vallegrande.sigei.civicDates.domain.port.in.ManageEventUseCase;
import pe.edu.vallegrande.sigei.civicDates.domain.port.out.EventRepository;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.entity.EventTypeImageEntity;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.repository.R2dbcEventTypeImageRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventService implements ManageEventUseCase {

    private final EventRepository eventRepository;
    private final NotificationService notificationService;
    private final R2dbcEventTypeImageRepository eventTypeImageRepository;

    @Override
    public Mono<Event> createEvent(Event event) {
        event.setStatus(EventStatus.ACTIVE);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());

        // If no custom image, resolve default from DB
        Mono<Event> enriched = (event.getImageUrl() == null || event.getImageUrl().isBlank())
                ? eventTypeImageRepository.findById(event.getEventType())
                        .map(img -> {
                            event.setImageUrl(img.getImageUrl());
                            return event;
                        })
                        .defaultIfEmpty(event)
                : Mono.just(event);

        return enriched.flatMap(e -> eventRepository.save(e)
                .flatMap(savedEvent -> {
                    if (savedEvent != null) {
                        savedEvent.setNotificationChannels(e.getNotificationChannels());
                        savedEvent.setCustomMessage(e.getCustomMessage());
                        savedEvent.setTargetRoles(e.getTargetRoles());
                        savedEvent.setIsNotificationScheduled(e.getIsNotificationScheduled());
                        
                        boolean isIncidencia = "INCIDENTE".equalsIgnoreCase(savedEvent.getEventType());
                        boolean isSendImmediately = Boolean.TRUE.equals(e.getSendImmediately());

                        if (isIncidencia || isSendImmediately) {
                            return notificationService.notify(savedEvent).thenReturn(savedEvent);
                        }
                    }
                    return Mono.justOrEmpty(savedEvent);
                }));
    }

    @Override
    public Mono<Event> updateEvent(Long id, Event event) {
        return eventRepository.findById(id)
                .switchIfEmpty(Mono.error(new EventNotFoundException(id)))
                .flatMap(existingEvent -> {
                    event.setId(id);
                    event.setUpdatedAt(LocalDateTime.now());
                    // If update doesn't provide image, keep existing
                    if (event.getImageUrl() == null || event.getImageUrl().isBlank()) {
                        event.setImageUrl(existingEvent.getImageUrl());
                    }
                    return eventRepository.update(event)
                            .flatMap(updatedEvent -> {
                                if (updatedEvent != null) {
                                    updatedEvent.setNotificationChannels(event.getNotificationChannels());
                                    updatedEvent.setCustomMessage(event.getCustomMessage());
                                    updatedEvent.setTargetRoles(event.getTargetRoles());
                                    
                                    boolean isSendImmediately = Boolean.TRUE.equals(event.getSendImmediately());
                                    if (isSendImmediately) {
                                        return notificationService.notify(updatedEvent).thenReturn(updatedEvent);
                                    }
                                }
                                return Mono.justOrEmpty(updatedEvent);
                            });
                });
    }

    @Override
    public Mono<Event> getEventById(Long id) {
        return eventRepository.findById(id)
                .switchIfEmpty(Mono.error(new EventNotFoundException(id)));
    }

    @Override
    public Flux<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Flux<Event> getEventsByInstitution(String institutionId) {
        return eventRepository.findByInstitutionId(institutionId);
    }

    @Override
    public Flux<Event> getInactiveEvents() {
        return eventRepository.findByStatus(EventStatus.INACTIVE.name());
    }

    @Override
    public Mono<Void> deleteEvent(Long id) {
        return eventRepository.findById(id)
                .switchIfEmpty(Mono.error(new EventNotFoundException(id)))
                .flatMap(event -> {
                    event.setStatus(EventStatus.INACTIVE);
                    event.setUpdatedAt(LocalDateTime.now());
                    return eventRepository.update(event);
                })
                .then();
    }

    @Override
    public Mono<Event> restoreEvent(Long id) {
        return eventRepository.findById(id)
                .switchIfEmpty(Mono.error(new EventNotFoundException(id)))
                .flatMap(event -> {
                    event.setStatus(EventStatus.ACTIVE);
                    event.setUpdatedAt(LocalDateTime.now());
                    return eventRepository.update(event);
                });
    }

    // ── Event Type Image Management ──────────────────────────────────────────

    public Flux<Map<String, String>> getAllDefaultImages() {
        return eventTypeImageRepository.findAll()
                .map(entity -> Map.of("eventType", entity.getEventType(), "imageUrl", entity.getImageUrl()));
    }

    public Mono<EventTypeImageEntity> updateDefaultImage(String eventType, String imageUrl) {
        return eventTypeImageRepository.findById(eventType)
                .flatMap(existing -> {
                    existing.setImageUrl(imageUrl);
                    return eventTypeImageRepository.save(existing);
                })
                .switchIfEmpty(
                    Mono.defer(() -> {
                        EventTypeImageEntity newEntity = EventTypeImageEntity.builder()
                                .eventType(eventType)
                                .imageUrl(imageUrl)
                                .isNewFlag(true)
                                .build();
                        return eventTypeImageRepository.save(newEntity);
                    })
                );
    }
}
