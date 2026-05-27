package pe.edu.vallegrande.sigei.civicDates.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCalendarRequest {
    private Integer academicYear;
    private String academicYearName;
    private LocalDate startDate;
    private LocalDate endDate;
}
