package pe.edu.vallegrande.sigei.civicDates.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InstitutionMismatchExceptionTest {

    @Test
    void constructorWithDetails_shouldBuildExpectedMessage() {
        InstitutionMismatchException ex = new InstitutionMismatchException(10L, "INST-A", "INST-B");

        assertTrue(ex.getMessage().contains("Event 10"));
        assertTrue(ex.getMessage().contains("INST-A"));
        assertTrue(ex.getMessage().contains("INST-B"));
    }

    @Test
    void constructorWithMessage_shouldPreserveMessage() {
        InstitutionMismatchException ex = new InstitutionMismatchException("custom");

        assertTrue(ex.getMessage().contains("custom"));
    }
}
