package pe.com.creditya.model.common.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserAlreadyExistsExceptionTest {
    @Test
    void shouldCreateExceptionWithCorrectMessage() {
        String email = "test@example.com";

        UserAlreadyExistsException exception = new UserAlreadyExistsException(email);

        assertThat(exception)
                .hasMessage("Usuario existe con el email: " + email)
                .hasNoCause();
    }
}