package pe.com.creditya.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import pe.com.creditya.api.config.UserPath;
import pe.com.creditya.api.dtos.ErrorResponseDto;
import pe.com.creditya.api.dtos.UserRequest;
import pe.com.creditya.api.dtos.UserResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {
    private final UserPath userPath;
    private final Handler userHandler;

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenSaveUser",
                    operation = @Operation(
                            operationId = "listenSaveUser",
                            summary = "Crear nuevo Usuario",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UserRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Usuario creado correctamente",
                                            content = @Content(
                                                    schema = @Schema(implementation = UserResponse.class),
                                                    mediaType = "application/json"
                                            )
                                    ),
                                    @ApiResponse(responseCode = "400",
                                            description = "Datos inválidos",
                                            content = @Content(
                                                    schema = @Schema(implementation = ErrorResponseDto.class),
                                                    mediaType = "application/json"
                                            )
                                    ),
                                    @ApiResponse(responseCode = "409",
                                            description = "Email ya existe",
                                            content = @Content(
                                                    schema = @Schema(implementation = ErrorResponseDto.class),
                                                    mediaType = "application/json"
                                            )
                                    ),
                                    @ApiResponse(responseCode = "400",
                                            description = "Not Found",
                                            content = @Content(
                                                    schema = @Schema(implementation = ErrorResponseDto.class),
                                                    mediaType = "application/json"
                                            )
                                    ), @ApiResponse(responseCode = "404",
                                    description = "Bad Request",
                                    content = @Content(
                                            schema = @Schema(implementation = ErrorResponseDto.class),
                                            mediaType = "application/json"
                                    )
                            ),
                                    @ApiResponse(responseCode = "500",
                                            description = "Error general",
                                            content = @Content(
                                                    schema = @Schema(implementation = ErrorResponseDto.class),
                                                    mediaType = "application/json"
                                            )
                                    )

                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction() {
        return route(POST(userPath.getUsers()), userHandler::listenSaveUser);
    }

}