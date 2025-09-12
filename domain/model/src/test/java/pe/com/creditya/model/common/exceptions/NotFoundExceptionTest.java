package pe.com.creditya.model.common.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotFoundExceptionTest {
    @Test
    void testExceptionCreationWithMessage() {
        String errorMessage = "User with id 123 not found";

        NotFoundException exception = new NotFoundException(errorMessage);
        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }
}