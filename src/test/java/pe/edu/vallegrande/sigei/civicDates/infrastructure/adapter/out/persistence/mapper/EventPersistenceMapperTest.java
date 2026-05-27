package pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.mapper;

import org.junit.jupiter.api.Test;
import pe.edu.vallegrande.sigei.civicDates.domain.model.Event;
import pe.edu.vallegrande.sigei.civicDates.domain.model.enums.EventStatus;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.entity.EventEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventPersistenceMapperTest {

    private final EventPersistenceMapper mapper = new EventPersistenceMapper();

    @Test
    void toEntity_shouldMapAllFields() {
        Event event = Event.builder()
                .id(10L)
                .institutionId("INST-1")
                .title("Acto")
                .description("Desc")
                .startDate(LocalDate.of(2026, 6, 1))
                .endDate(LocalDate.of(2026, 6, 2))
                .eventType("CIVICO")
                .isHoliday(true)
                .isRecurring(false)
                .isNational(true)
                .affectsClasses(true)
                .createdBy("admin")
                .status(EventStatus.INACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        EventEntity entity = mapper.toEntity(event);

        assertEquals(10L, entity.getId());
        assertEquals("INST-1", entity.getInstitutionId());
        assertEquals("INACTIVE", entity.getStatus());
    }

    @Test
    void toEntity_shouldDefaultStatusWhenNull() {
        Event event = Event.builder().title("Sin estado").build();

        EventEntity entity = mapper.toEntity(event);

        assertEquals("ACTIVE", entity.getStatus());
    }

    @Test
    void toDomain_shouldMapAllFields() {
        EventEntity entity = EventEntity.builder()
                .id(11L)
                .institutionId("INST-2")
                .title("Festival")
                .description("Desc")
                .startDate(LocalDate.of(2026, 7, 1))
                .endDate(LocalDate.of(2026, 7, 1))
                .eventType("CULTURAL")
                .isHoliday(false)
                .isRecurring(true)
                .isNational(false)
                .affectsClasses(false)
                .createdBy("user")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Event domain = mapper.toDomain(entity);

        assertEquals(11L, domain.getId());
        assertEquals("INST-2", domain.getInstitutionId());
        assertEquals(EventStatus.ACTIVE, domain.getStatus());
    }
}
