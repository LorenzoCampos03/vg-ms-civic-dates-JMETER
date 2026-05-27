package pe.edu.vallegrande.sigei.civicDates.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.vallegrande.sigei.civicDates.domain.model.enums.CalendarStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicCalendar {
    private Integer id;
    private String institutionId;
    private Integer academicYear;
    private String academicYearName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CalendarStatus status;
}
