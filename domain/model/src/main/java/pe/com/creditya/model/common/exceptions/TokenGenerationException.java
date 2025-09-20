package pe.com.creditya.model.common.exceptions;

public class TokenGenerationException extends JwtTokenException {
    public TokenGenerationException(String message) {
        super(message);
    }

    public TokenGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}