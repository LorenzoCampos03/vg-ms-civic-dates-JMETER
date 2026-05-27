package pe.edu.vallegrande.sigei.civicDates.application.mapper;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.sigei.civicDates.application.dto.request.CreateEventRequest;
import pe.edu.vallegrande.sigei.civicDates.application.dto.request.UpdateEventRequest;
import pe.edu.vallegrande.sigei.civicDates.application.dto.response.EventResponse;
import pe.edu.vallegrande.sigei.civicDates.domain.model.Event;
import pe.edu.vallegrande.sigei.civicDates.domain.model.enums.EventStatus;

import java.time.LocalDateTime;

@Component
public class EventMapper {

    public Event toEvent(CreateEventRequest request) {
        return Event.builder()
                .institutionId(request.getInstitutionId())
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .eventType(request.getEventType())
                .isHoliday(request.getIsHoliday())
                .isRecurring(request.getIsRecurring())
                .isNational(request.getIsNational())
                .affectsClasses(request.getAffectsClasses())
                .createdBy(request.getCreatedBy())
                .notificationChannels(request.getNotificationChannels())
                .customMessage(request.getCustomMessage())
                .targetRoles(request.getTargetRoles())
                .isNotificationScheduled(request.getIsNotificationScheduled() != null ? request.getIsNotificationScheduled() : false)
                .sendImmediately(request.getSendImmediately() != null ? request.getSendImmediately() : false)
                .imageUrl(request.getImageUrl())
                .status(EventStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Event toEvent(UpdateEventRequest request, Event existingEvent) {
        existingEvent.setTitle(request.getTitle());
        existingEvent.setDescription(request.getDescription());
        existingEvent.setStartDate(request.getStartDate());
        existingEvent.setEndDate(request.getEndDate());
        existingEvent.setEventType(request.getEventType());
        existingEvent.setIsHoliday(request.getIsHoliday());
        existingEvent.setIsRecurring(request.getIsRecurring());
        existingEvent.setIsNational(request.getIsNational());
        existingEvent.setAffectsClasses(request.getAffectsClasses());
        existingEvent.setNotificationChannels(request.getNotificationChannels());
        existingEvent.setCustomMessage(request.getCustomMessage());
        existingEvent.setTargetRoles(request.getTargetRoles());
        existingEvent.setIsNotificationScheduled(request.getIsNotificationScheduled() != null ? request.getIsNotificationScheduled() : false);
        existingEvent.setSendImmediately(request.getSendImmediately() != null ? request.getSendImmediately() : false);
        if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            existingEvent.setImageUrl(request.getImageUrl());
        }
        existingEvent.setUpdatedAt(LocalDateTime.now());
        return existingEvent;
    }

    public EventResponse toResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .institutionId(event.getInstitutionId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .eventType(event.getEventType())
                .isHoliday(event.getIsHoliday())
                .isRecurring(event.getIsRecurring())
                .isNational(event.getIsNational())
                .affectsClasses(event.getAffectsClasses())
                .createdBy(event.getCreatedBy())
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .imageUrl(event.getImageUrl())
                .build();
    }
}
