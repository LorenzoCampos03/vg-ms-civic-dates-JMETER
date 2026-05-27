package pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("event")
public class EventEntity {
    
    @Id
    @Column("id")
    private Long id;
    
    @Column("institution_id")
    private String institutionId;
    
    @Column("title")
    private String title;
    
    @Column("description")
    private String description;
    
    @Column("start_date")
    private LocalDate startDate;
    
    @Column("end_date")
    private LocalDate endDate;
    
    @Column("event_type")
    private String eventType;
    
    @Column("is_holiday")
    private Boolean isHoliday;
    
    @Column("is_recurring")
    private Boolean isRecurring;
    
    @Column("is_national")
    private Boolean isNational;
    
    @Column("affects_classes")
    private Boolean affectsClasses;
    
    @Column("created_by")
    private String createdBy;
    
    @Column("status")
    private String status;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("image_url")
    private String imageUrl;
}
