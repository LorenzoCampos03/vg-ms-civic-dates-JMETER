package pe.edu.vallegrande.sigei.civicDates.domain.port.out;

import pe.edu.vallegrande.sigei.civicDates.domain.model.AcademicCalendar;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AcademicCalendarRepository {
    Mono<AcademicCalendar> save(AcademicCalendar calendar);
    Mono<AcademicCalendar> findById(Integer id);
    Flux<AcademicCalendar> findAll();
    Flux<AcademicCalendar> findByInstitutionId(String institutionId);
    Mono<AcademicCalendar> update(AcademicCalendar calendar);
}
