package pe.com.creditya.model.common.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class BusinessValidationExceptionTest {
    @Test
    void shouldCreateExceptionWithFieldAndMessage() {
        // Given
        String field = "email";
        String message = "must be a valid email address";

        BusinessValidationException exception =
                new BusinessValidationException(field, message);

        assertThat(exception).isNotNull();
    }

}