package pe.com.creditya.security.common.constants;

public final class AuthConstants {
    private AuthConstants() {}

    // Mensajes de error
    public static final String NOT_SENDED_TOKEN = "Token no proporcionado";
    public static final String INVALID_JWT_TOKEN = "Token JWT inválido";
    public static final String TOKEN_VALIDATION_FAILED = "Error validando token";
    public static final String AUTHORITIES_EXTRACTION_FAILED = "Error extrayendo autoridades del token";

    // Mensajes de log
    public static final String LOGGER_SUCCESS_AUTHENTICATION = "Autenticación exitosa para usuario: {}";
    public static final String LOGGER_FAILED_AUTHENTICATION = "Autenticación fallida para token: {} - Error: {}";
    public static final String LOGGER_AUTHENTICATION_FAILED = "Error en proceso de autenticación: {}";
    public static final String LOGGER_ERROR_JWT_USER = "Usuario {} no tiene roles definidos en el token";
    public static final String LOGGER_ERROR_AUTHORITIES = "Error extrayendo autoridades: {}";

    // Roles y prefijos
    public static final String PREFIX_ROLES = "roles";
    public static final String PREFIX_TOKEN = "ROLE_";
    public static final String ROLE_USER = "ROLE_USER";

    // Validaciones
    public static final String CREDENTIALS_NULL_MESSAGE = "Credenciales no pueden ser nulas";

    public static final String TOKEN_GENERATION_ERROR = "Error generando token JWT";
    public static final String TOKEN_PARSING_ERROR = "Error parseando token JWT";
    public static final String TOKEN_EXPIRED = "Token JWT expirado";
    public static final String UNEXPECTED_ERROR = "Error inesperado procesando token";

    // Mensajes de log
    public static final String LOGGER_TOKEN_GENERATION_ERROR = "Error generando token para usuario: {} - Error: {}";
    public static final String LOGGER_EXPIRED_TOKEN = "Token expirado: {}";
    public static final String LOGGER_TOKEN_PARSING_ERROR = "Error parseando token: {} - Error: {}";
    public static final String LOGGER_UNEXPECTED_ERROR = "Error inesperado para usuario: {} - Error: {}";
    public static final String LOGGER_VALIDATION_ERROR = "Error validando token: {}";
}