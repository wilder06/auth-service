package pe.com.creditya.model.exceptions;

import pe.com.creditya.model.constants.UserConstants;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super(UserConstants.LOGGER_USER_EXISTS + email);
    }
}