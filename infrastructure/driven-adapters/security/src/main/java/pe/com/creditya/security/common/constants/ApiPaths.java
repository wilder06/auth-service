package pe.com.creditya.security.common.constants;

public class ApiPaths {
    private ApiPaths() {}
    public static final String AUTH_BASE = "/api/v1";
    public static final String LOGIN = AUTH_BASE + "/login";
    public static final String REGISTER = AUTH_BASE + "/usuarios/register";
    public static final String APPLICATIONS =AUTH_BASE + "/usuarios/by-emails";

    public static final String SWAGGER_UI = "/swagger-ui.html";
    public static final String SWAGGER_UI_ALL = "/swagger-ui/**";
    public static final String API_DOCS_ALL = "/v3/api-docs/**";
    public static final String WEBJARS_ALL = "/webjars/**";
    public static final String SWAGGER_RESOURCES_ALL = "/swagger-resources/**";
}
