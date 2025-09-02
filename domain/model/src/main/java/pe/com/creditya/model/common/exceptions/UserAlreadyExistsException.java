package pe.com.creditya.model.common.exceptions;

import pe.com.creditya.model.common.constants.LogConstants;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super(LogConstants.LOGGER_USER_EXISTS_EMAIL + email);
    }
}