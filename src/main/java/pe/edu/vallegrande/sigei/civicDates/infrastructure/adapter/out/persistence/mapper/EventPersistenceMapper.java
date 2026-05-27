package pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.sigei.civicDates.domain.model.Event;
import pe.edu.vallegrande.sigei.civicDates.domain.model.enums.EventStatus;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.entity.EventEntity;

@Component
public class EventPersistenceMapper {

    public EventEntity toEntity(Event event) {
        return EventEntity.builder()
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
                .status(event.getStatus() != null ? event.getStatus().name() : EventStatus.ACTIVE.name())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .imageUrl(event.getImageUrl())
                .build();
    }

    public Event toDomain(EventEntity entity) {
        return Event.builder()
                .id(entity.getId())
                .institutionId(entity.getInstitutionId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .eventType(entity.getEventType())
                .isHoliday(entity.getIsHoliday())
                .isRecurring(entity.getIsRecurring())
                .isNational(entity.getIsNational())
                .affectsClasses(entity.getAffectsClasses())
                .createdBy(entity.getCreatedBy())
                .status(EventStatus.valueOf(entity.getStatus()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .imageUrl(entity.getImageUrl())
                .build();
    }
}
