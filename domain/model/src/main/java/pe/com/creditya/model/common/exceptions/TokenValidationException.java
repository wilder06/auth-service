package pe.com.creditya.model.common.exceptions;

public class TokenValidationException extends AuthenticationException {
    public TokenValidationException(String message) {
        super(message);
    }

    public TokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}