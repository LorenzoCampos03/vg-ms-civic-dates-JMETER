package pe.edu.vallegrande.sigei.civicDates.domain.exception;

public class InstitutionMismatchException extends RuntimeException {
    public InstitutionMismatchException(Long eventId, String eventInstitutionId, String calendarInstitutionId) {
        super(String.format("Event %d belongs to institution '%s' and cannot be added to calendar of institution '%s'. Only recurring events can be shared across institutions.",
                eventId, eventInstitutionId, calendarInstitutionId));
    }
    
    public InstitutionMismatchException(String message) {
        super(message);
    }
}
