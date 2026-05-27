package pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("event_calendar")
public class EventCalendarEntity {
    
    @Id
    @Column("id")
    private Integer id;
    
    @Column("calendar_id")
    private Integer calendarId;
    
    @Column("event_id")
    private Long eventId;
    
    @Column("created_at")
    private LocalDateTime createdAt;
}
