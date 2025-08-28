package pe.com.creditya.model.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TechnicalExceptionTest {

    @Test
    void shouldCreateExceptionWithMessageAndCause() {
        // given
        String message = "Error técnico";
        Throwable cause = new RuntimeException("Causa original");

        // when
        TechnicalException exception = new TechnicalException(message, cause);

        // then
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void shouldCreateExceptionWithNullCause() {
        // given
        String message = "Error sin causa";

        // when
        TechnicalException exception = new TechnicalException(message, null);

        // then
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
}