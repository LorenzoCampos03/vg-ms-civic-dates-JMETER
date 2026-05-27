package pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.mapper;

import org.junit.jupiter.api.Test;
import pe.edu.vallegrande.sigei.civicDates.domain.model.AcademicCalendar;
import pe.edu.vallegrande.sigei.civicDates.domain.model.EventCalendar;
import pe.edu.vallegrande.sigei.civicDates.domain.model.enums.CalendarStatus;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.entity.AcademicCalendarEntity;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.entity.EventCalendarEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalendarPersistenceMapperTest {

    private final CalendarPersistenceMapper mapper = new CalendarPersistenceMapper();

    @Test
    void toEntity_shouldMapAcademicCalendarAndDefaultStatus() {
        AcademicCalendar calendar = AcademicCalendar.builder()
                .id(1)
                .institutionId("INST")
                .academicYear(2026)
                .academicYearName("Año 2026")
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 12, 31))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        AcademicCalendarEntity entity = mapper.toEntity(calendar);

        assertEquals(1, entity.getId());
        assertEquals("ACTIVE", entity.getStatus());
    }

    @Test
    void toDomain_shouldMapAcademicCalendarAndDefaultNullStatus() {
        AcademicCalendarEntity entity = AcademicCalendarEntity.builder()
                .id(2)
                .institutionId("INST")
                .academicYear(2025)
                .academicYearName("Año 2025")
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .status(null)
                .build();

        AcademicCalendar calendar = mapper.toDomain(entity);

        assertEquals(2, calendar.getId());
        assertEquals(CalendarStatus.ACTIVE, calendar.getStatus());
    }

    @Test
    void eventCalendarRoundTrip_shouldMapBothDirections() {
        EventCalendar eventCalendar = EventCalendar.builder()
                .id(8)
                .calendarId(3)
                .eventId(30L)
                .createdAt(LocalDateTime.now())
                .build();

        EventCalendarEntity entity = mapper.toEntity(eventCalendar);
        EventCalendar domain = mapper.toDomain(entity);

        assertEquals(3, domain.getCalendarId());
        assertEquals(30L, domain.getEventId());
    }
}
