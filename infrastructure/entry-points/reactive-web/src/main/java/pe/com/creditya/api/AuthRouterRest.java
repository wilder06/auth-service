package pe.com.creditya.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import pe.com.creditya.api.common.UserPath;
import pe.com.creditya.api.dtos.*;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class AuthRouterRest {
    private final UserPath userPath;
    private final AuthHandler authHandler;

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/login",
                    method = RequestMethod.POST,
                    beanClass = AuthHandler.class,
                    beanMethod = "listenLogin",
                    operation = @Operation(
                            operationId = "loginUser",
                            summary = "Login de usuario",
                            description = "Autentica usuario con email y contraseña. Retorna JWT",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = LoginRequest.class),
                                            mediaType = "application/json"
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Login exitoso",
                                            content = @Content(
                                                    schema = @Schema(implementation = AuthTokenResponse.class),
                                                    mediaType = "application/json"
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Solicitud inválida",
                                            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "Credenciales inválidas",
                                            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Error interno",
                                            content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> authRouterFunction() {
        return route(POST(userPath.getLogin()), authHandler::listenLogin);
    }
}

