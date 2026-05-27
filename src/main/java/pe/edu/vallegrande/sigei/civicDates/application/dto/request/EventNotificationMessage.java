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
public class EventNotificationMessage {
    private Long eventId;
    private String eventType;
    private String institutionId;
    private String title;
    private String description;
    private LocalDate eventDate;
    private List<String> channels;
    private String customMessage;
    private List<String> targetRoles;
    private String imageUrl;
    private String token;
}

