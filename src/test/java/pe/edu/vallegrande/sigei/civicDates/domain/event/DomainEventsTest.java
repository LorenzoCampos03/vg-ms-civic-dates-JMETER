package pe.edu.vallegrande.sigei.civicDates.domain.event;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DomainEventsTest {

    @Test
    void civicEventCreatedEvent_shouldExposeRecordFields() {
        CivicEventCreatedEvent event = new CivicEventCreatedEvent(1L, "INST-1", "Acto", LocalDate.of(2026, 6, 15), true);

        assertEquals(1L, event.eventId());
        assertEquals("INST-1", event.institutionId());
        assertEquals("Acto", event.title());
        assertEquals(LocalDate.of(2026, 6, 15), event.startDate());
        assertEquals(true, event.isHoliday());
    }

    @Test
    void eventReminderEvent_shouldExposeRecordFields() {
        EventReminderEvent event = new EventReminderEvent(2L, "INST-2", "Festival", 3);

        assertEquals(2L, event.eventId());
        assertEquals("INST-2", event.institutionId());
        assertEquals("Festival", event.title());
        assertEquals(3, event.daysUntilEvent());
    }
}
