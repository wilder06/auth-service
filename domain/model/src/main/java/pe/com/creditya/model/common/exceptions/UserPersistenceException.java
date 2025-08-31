package pe.com.creditya.model.common.exceptions;

public class UserPersistenceException extends RuntimeException {
    public UserPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}