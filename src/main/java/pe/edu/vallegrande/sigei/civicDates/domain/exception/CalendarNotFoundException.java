package pe.edu.vallegrande.sigei.civicDates.domain.exception;

public class CalendarNotFoundException extends RuntimeException {
    public CalendarNotFoundException(Integer id) {
        super("Calendar not found with id: " + id);
    }
    
    public CalendarNotFoundException(String message) {
        super(message);
    }
}
