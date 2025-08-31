package pe.com.creditya.api.handler;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.mock.web.server.MockServerWebExchange;
import pe.com.creditya.api.dtos.ErrorResponseDto;
import pe.com.creditya.model.exceptions.TechnicalException;
import pe.com.creditya.model.exceptions.UserAlreadyExistsException;
import pe.com.creditya.model.user.User;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleUserAlreadyExistsException() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("email@test.com");

        Mono<ResponseEntity<ErrorResponseDto>> responseMono = handler.handleExists(ex);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(response.getBody().message())
                            .contains("email@test.com");
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleTechnicalException() {
        TechnicalException ex = new TechnicalException("Error inesperado en registro de usuario", new RuntimeException());

        Mono<ResponseEntity<ErrorResponseDto>> responseMono = handler.handleTechnical(ex);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    assertThat(response.getBody().message()).isEqualTo("Error inesperado en registro de usuario");
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleFallbackException() {
        Exception ex = new Exception("any error");

        Mono<ResponseEntity<ErrorResponseDto>> responseMono = handler.fallback(ex);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    assertThat(response.getBody().message()).isEqualTo("Unexpected error");
                })
                .verifyComplete();
    }

    @Test
    void testHandleNoResourceFound() {
        NoResourceFoundException ex = new NoResourceFoundException("Not found");

        ServerHttpRequest request = MockServerHttpRequest
                .get("/api/test")
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from((MockServerHttpRequest) request);

        StepVerifier.create(handler.handleNoResourceFound(ex, exchange))
                .assertNext(responseEntity -> {
                    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(responseEntity.getBody()).isNotNull();
                    assertThat(responseEntity.getBody().message())
                            .contains("El recurso solicitado no existe: /api/test");
                    assertThat(responseEntity.getBody().code()).isEqualTo("404");
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleConstraintViolationException() {
        ConstraintViolation<User> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);

        when(path.toString()).thenReturn("email");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("El correo electrónico no puede estar vacío");

        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        Mono<ResponseEntity<ErrorResponseDto>> responseMono = handler.handleValidation(ex);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(response.getBody().message()).contains("email: El correo electrónico no puede estar vacío");
                })
                .verifyComplete();
    }
}