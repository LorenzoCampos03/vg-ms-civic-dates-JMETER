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
@Table("academic_calendar")
public class AcademicCalendarEntity {
    
    @Id
    @Column("id")
    private Integer id;
    
    @Column("institution_id")
    private String institutionId;
    
    @Column("academic_year")
    private Integer academicYear;
    
    @Column("academic_year_name")
    private String academicYearName;
    
    @Column("start_date")
    private LocalDate startDate;
    
    @Column("end_date")
    private LocalDate endDate;
    
    @Column("status")
    private String status;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
