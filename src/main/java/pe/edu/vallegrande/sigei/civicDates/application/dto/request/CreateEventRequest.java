package pe.edu.vallegrande.sigei.civicDates.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {
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
    private List<String> notificationChannels; // e.g. "EMAIL", "WHATSAPP"
    private String customMessage;
    private List<String> targetRoles;
    private Boolean isNotificationScheduled;
    private Boolean sendImmediately;
    private String imageUrl;
}
