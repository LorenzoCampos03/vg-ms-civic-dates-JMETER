package pe.edu.vallegrande.sigei.civicDates.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCalendar {
    private Integer id;
    private Integer calendarId;
    private Long eventId;
    private LocalDateTime createdAt;
}
