package pe.edu.vallegrande.sigei.civicDates.domain.event;

public record EventReminderEvent(
    Long eventId,
    String institutionId,
    String title,
    Integer daysUntilEvent
) {
}
