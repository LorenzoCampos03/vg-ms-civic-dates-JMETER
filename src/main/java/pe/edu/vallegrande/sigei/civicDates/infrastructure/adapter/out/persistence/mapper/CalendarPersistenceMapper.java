package pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.sigei.civicDates.domain.model.AcademicCalendar;
import pe.edu.vallegrande.sigei.civicDates.domain.model.EventCalendar;
import pe.edu.vallegrande.sigei.civicDates.domain.model.enums.CalendarStatus;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.entity.AcademicCalendarEntity;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.entity.EventCalendarEntity;

@Component
public class CalendarPersistenceMapper {

    public AcademicCalendarEntity toEntity(AcademicCalendar calendar) {
        return AcademicCalendarEntity.builder()
                .id(calendar.getId())
                .institutionId(calendar.getInstitutionId())
                .academicYear(calendar.getAcademicYear())
                .academicYearName(calendar.getAcademicYearName())
                .startDate(calendar.getStartDate())
                .endDate(calendar.getEndDate())
                .status(calendar.getStatus() != null ? calendar.getStatus().toString() : "ACTIVE")
                .createdAt(calendar.getCreatedAt())
                .updatedAt(calendar.getUpdatedAt())
                .build();
    }

    public AcademicCalendar toDomain(AcademicCalendarEntity entity) {
        return AcademicCalendar.builder()
                .id(entity.getId())
                .institutionId(entity.getInstitutionId())
                .academicYear(entity.getAcademicYear())
                .academicYearName(entity.getAcademicYearName())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus() != null ? CalendarStatus.valueOf(entity.getStatus()) : CalendarStatus.ACTIVE)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public EventCalendarEntity toEntity(EventCalendar eventCalendar) {
        return EventCalendarEntity.builder()
                .id(eventCalendar.getId())
                .calendarId(eventCalendar.getCalendarId())
                .eventId(eventCalendar.getEventId())
                .createdAt(eventCalendar.getCreatedAt())
                .build();
    }

    public EventCalendar toDomain(EventCalendarEntity entity) {
        return EventCalendar.builder()
                .id(entity.getId())
                .calendarId(entity.getCalendarId())
                .eventId(entity.getEventId())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
