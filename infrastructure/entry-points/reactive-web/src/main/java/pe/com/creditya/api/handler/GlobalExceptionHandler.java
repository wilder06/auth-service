package pe.com.creditya.api.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;

import pe.com.creditya.api.dtos.ErrorResponseDto;
import pe.com.creditya.model.exceptions.TechnicalException;
import pe.com.creditya.model.exceptions.UserAlreadyExistsException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleValidation(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
        return Mono.just(ResponseEntity.badRequest()
                .body(new ErrorResponseDto(HttpStatus.BAD_REQUEST.toString(), String.join(", ", errors))));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleExists(UserAlreadyExistsException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(HttpStatus.CONFLICT.toString(), ex.getMessage())));
    }
    @ExceptionHandler(TechnicalException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleTechnical(TechnicalException ex) {
        return Mono.just(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage()))
        );
    }
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponseDto>> fallback(Exception ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Unexpected error")));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleNoResourceFound(NoResourceFoundException ex, ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                String.valueOf(HttpStatus.NOT_FOUND.value()),
                "El recurso solicitado no existe: " + path,
                Instant.now()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
    }
}