package pe.edu.vallegrande.sigei.civicDates.domain.event;

import java.time.LocalDate;

public record CivicEventCreatedEvent(
    Long eventId,
    String institutionId,
    String title,
    LocalDate startDate,
    Boolean isHoliday
) {
}
