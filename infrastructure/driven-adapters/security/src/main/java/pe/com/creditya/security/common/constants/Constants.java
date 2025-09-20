package pe.com.creditya.security.common.constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Constants {
    public static final String TOKEN_ATTRIBUTE = "token";
    public static final String INVALID_JWT_TOKEN = "Invalid JWT token or Expired";
    public static final String MESSAGE_NOT_SUPPORTED = "Save not supported";
    public static final String PREFIX_BEARER = "Bearer ";
    public static final String PREFIX_CERT = "JCEKS";
    public static final String PREFIX_ROLES = "roles";
    public static final String USER_ID = "userId";
    public static final String LOGGER_EXPIRED_TOKEN = "Invalid/expired token";
    public static final String LOGGER_ERROR_ALIAS = "No private key found for alias";
    public static final String LOGGER_ERROR_KEY = "Failed to load JWT key pair from keystore";
    public static final String LOGGER_AUTH_STARTED = "Autenticación iniciada para token: {}";
    public static final String LOGGER_AUTH_SUCCESS = "Autenticación exitosa para usuario: {}";
    public static final String LOGGER_AUTH_FAILED = "Autenticación fallida para token: {} - Error: {}";
    public static final String LOGGER_LOAD_OPERATION_FAILED = "Error en operación load para URI: {} - Error: {}";
    public static final String LOGGER_NO_TOKEN_PRESENT = "No se encontró token en la solicitud: {}";
    public static final String LOGGER_TOKEN_EXTRACTION_ERROR = "Error extrayendo token de URI: {} - Error: {}";
    public static final String LOGGER_TOKEN_FOUND_HEADER = "Token encontrado en header para URI: {}";
    public static final String LOGGER_TOKEN_FOUND_QUERY = "Token encontrado en query param para URI: {}";
    public static final String LOGGER_TOKEN_NOT_FOUND = "Token no encontrado en la solicitud: {}";
    public static final String LOGGER_TOKEN_GENERATED_SUCCESS = "Token generado exitosamente para usuario: {}";
    public static final String LOGGER_USER_NOT_FOUND = "Usuario no encontrado: {}";
    public static final String LOGGER_USER_FOUND = "Usuario encontrado con ID: {}";
    public static final String LOGGER_INVALID_PASSWORD = "Password inválido para usuario: {}";
    public static final String LOGGER_PASSWORD_VALIDATED = "Password validado exitosamente para usuario: {}";
    public static final String LOGGER_TOKEN_CREATED = "Token creado para usuario: {}";
    public static final String LOGGER_ROLE_NOT_FOUND = "Rol no encontrado para ID: {}";
    public static final String LOGGER_AUTHENTICATION_FAILED = "Autenticación fallida para usuario: {} - Error: {}";
    public static final String LOGGER_ROLE_ERROR = "Error de rol para usuario: {} - Error: {}";
    public static final String LOGGER_TOKEN_GENERATION_ERROR = "Error generando token para usuario: {} - Error: {}";

    public static final String INVALID_CREDENTIALS = "Credenciales inválidas password";
    public static final String PREFIX_TOKEN = "ROLE_";
}
