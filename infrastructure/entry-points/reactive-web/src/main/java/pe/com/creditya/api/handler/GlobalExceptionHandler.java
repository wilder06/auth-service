package pe.com.creditya.api.handler;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;

import pe.com.creditya.api.common.constants.UserConstants;
import pe.com.creditya.api.dtos.ErrorResponseDto;
import pe.com.creditya.model.common.exceptions.*;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleInvalidCredentials(InvalidCredentialsException ex, ServerWebExchange exchange) {
        log.warn("Invalid credentials: {}", ex.getMessage());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                ex.getMessage(),
                Instant.now(),
                exchange.getRequest().getPath().value()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse));
    }

    @ExceptionHandler({ TokenValidationException.class})
    public Mono<ResponseEntity<ErrorResponseDto>> handleJwtExceptions(RuntimeException ex, ServerWebExchange exchange) {
        log.warn("JWT validation error: {}", ex.getMessage());

        String errorMessage = ex instanceof ExpiredJwtException ?
                "Token expired" : "Token validation failed";

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                errorMessage,
                Instant.now(),
                exchange.getRequest().getPath().value()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleExists(UserAlreadyExistsException ex, ServerWebExchange exchange) {
        log.warn("User already exists: {}", ex.getMessage());
        return handleExceptionInternal(ex, HttpStatus.CONFLICT, ex.getMessage(), exchange);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleValidation(ConstraintViolationException ex, ServerWebExchange exchange) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toList());

        String errorMessage = String.join(", ", errors);
        log.warn("Validation error: {}", errorMessage);

        return handleExceptionInternal(ex, HttpStatus.BAD_REQUEST, errorMessage, exchange);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleNoResourceFound(NoResourceFoundException ex, ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        String errorMessage = UserConstants.NOT_FOUNT_RESOURCE + path;

        log.warn("Resource not found: {}", errorMessage);
        return handleExceptionInternal(ex, HttpStatus.NOT_FOUND, errorMessage, exchange);
    }

    @ExceptionHandler({TechnicalException.class, TokenGenerationException.class, UserPersistenceException.class})
    public Mono<ResponseEntity<ErrorResponseDto>> handleTechnicalExceptions(RuntimeException ex, ServerWebExchange exchange) {
        log.error("Technical error: {}", ex.getMessage(), ex);
        return handleExceptionInternal(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), exchange);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleGlobalException(Exception ex, ServerWebExchange exchange) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return handleExceptionInternal(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), exchange);
    }

    private Mono<ResponseEntity<ErrorResponseDto>> handleExceptionInternal(
            Exception ex, HttpStatus status, String message, ServerWebExchange exchange) {

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                String.valueOf(status.value()),
                message,
                Instant.now(),
                exchange.getRequest().getPath().value()
        );

        return Mono.just(ResponseEntity.status(status).body(errorResponse));
    }
    @ExceptionHandler(AuthenticationException.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleAuthenticationException(AuthenticationException ex, ServerWebExchange exchange) {
        log.warn("Authentication error: {}", ex.getMessage());

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                ex.getMessage(),
                Instant.now(),
                exchange.getRequest().getPath().value()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse));
    }
}
