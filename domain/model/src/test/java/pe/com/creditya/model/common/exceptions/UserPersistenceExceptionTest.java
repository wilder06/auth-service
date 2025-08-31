package pe.com.creditya.model.common.exceptions;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserPersistenceExceptionTest {
    @Test
    void shouldCreateUserPersistenceExceptionWithMessageAndCause() {
        // Given
        String message = "Error saving user";
        Throwable cause = new RuntimeException("Database connection failed");

        // When
        UserPersistenceException exception = new UserPersistenceException(message, cause);

        // Then
        assertThat(exception)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error saving user")
                .hasCause(cause);
    }
}