package pe.edu.vallegrande.sigei.civicDates.application.mapper;

import org.springframework.stereotype.Component;
import pe.edu.vallegrande.sigei.civicDates.application.dto.request.CreateCalendarRequest;
import pe.edu.vallegrande.sigei.civicDates.application.dto.response.CalendarResponse;
import pe.edu.vallegrande.sigei.civicDates.application.dto.response.CalendarWithEventsResponse;
import pe.edu.vallegrande.sigei.civicDates.application.dto.response.EventResponse;
import pe.edu.vallegrande.sigei.civicDates.domain.model.AcademicCalendar;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CalendarMapper {

    public AcademicCalendar toCalendar(CreateCalendarRequest request) {
        return AcademicCalendar.builder()
                .institutionId(request.getInstitutionId())
                .academicYear(request.getAcademicYear())
                .academicYearName(request.getAcademicYearName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public CalendarResponse toResponse(AcademicCalendar calendar) {
        return CalendarResponse.builder()
                .id(calendar.getId())
                .institutionId(calendar.getInstitutionId())
                .academicYear(calendar.getAcademicYear())
                .academicYearName(calendar.getAcademicYearName())
                .startDate(calendar.getStartDate())
                .endDate(calendar.getEndDate())
                .status(calendar.getStatus())
                .createdAt(calendar.getCreatedAt())
                .updatedAt(calendar.getUpdatedAt())
                .build();
    }

    public CalendarWithEventsResponse toResponseWithEvents(AcademicCalendar calendar, List<EventResponse> events) {
        return CalendarWithEventsResponse.builder()
                .id(calendar.getId())
                .institutionId(calendar.getInstitutionId())
                .academicYear(calendar.getAcademicYear())
                .academicYearName(calendar.getAcademicYearName())
                .startDate(calendar.getStartDate())
                .endDate(calendar.getEndDate())
                .status(calendar.getStatus())
                .createdAt(calendar.getCreatedAt())
                .updatedAt(calendar.getUpdatedAt())
                .events(events)
                .build();
    }
}
