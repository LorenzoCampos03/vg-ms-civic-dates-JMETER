package pe.edu.vallegrande.sigei.civicDates.infrastructure.adapter.in.rest;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pe.edu.vallegrande.sigei.civicDates.domain.exception.CalendarNotFoundException;
import pe.edu.vallegrande.sigei.civicDates.domain.exception.EventNotFoundException;
import pe.edu.vallegrande.sigei.civicDates.domain.exception.InstitutionMismatchException;
import pe.edu.vallegrande.sigei.civicDates.infrastructure.common.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleEventNotFoundException_shouldReturn404() {
        ResponseEntity<ErrorResponse> response = handler.handleEventNotFoundException(new EventNotFoundException(10L));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Event Not Found", response.getBody().getError());
    }

    @Test
    void handleCalendarNotFoundException_shouldReturn404() {
        ResponseEntity<ErrorResponse> response = handler.handleCalendarNotFoundException(new CalendarNotFoundException(22));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Calendar Not Found", response.getBody().getError());
    }

    @Test
    void handleInstitutionMismatchException_shouldReturn400() {
        ResponseEntity<ErrorResponse> response = handler.handleInstitutionMismatchException(
                new InstitutionMismatchException(9L, "INST-A", "INST-B"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Institution Mismatch", response.getBody().getError());
    }

    @Test
    void handleGenericException_shouldReturn500() {
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(new RuntimeException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("boom", response.getBody().getMessage());
    }
}
