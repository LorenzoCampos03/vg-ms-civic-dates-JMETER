package pe.edu.vallegrande.sigei.civicDates.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.sigei.civicDates.domain.model.enums.EventStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.annotation.Transient;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private Long id;
    private String institutionId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String eventType;  // "CIVICO", "CULTURAL", "RELIGIOSO", "INSTITUCIONAL", "INCIDENTE"
    private Boolean isHoliday;
    private Boolean isRecurring;
    private Boolean isNational;
    private Boolean affectsClasses;
    private String createdBy;
    private EventStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String imageUrl;
    
    // Notification parameters (Now saved to DB for scheduled jobs)
    private List<String> notificationChannels;
    private String customMessage;
    private List<String> targetRoles;
    private Boolean isNotificationScheduled;

    @Transient
    private Boolean sendImmediately;
}
