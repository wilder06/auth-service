package pe.com.creditya.security.common.constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Constants {
    public static final String TOKEN_ATTRIBUTE = "token";
    public static final String PREFIX_TOKEN = "ROLE_";
    public static final String ROLE_NOT_FOUND = "Role not found";
    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String INVALID_JWT_TOKEN = "Invalid JWT token";
    public static final String NOT_SENDED_TOKEN = "Token no proporcionado";
    public static final String MESSAGE_NOT_SUPPORTED = "Save not supported";
    public static final String PREFIX_BEARER = "Bearer ";
    public static final String PREFIX_ROLES = "roles";
    public static final String USER_ID = "userId";
    public static final String LOGGER_AUTHENTICATION_FAILED = "Authentication failed: {}";
    public static final String LOGGER_EXPIRED_TOKEN = "Invalid/expired token";
}
